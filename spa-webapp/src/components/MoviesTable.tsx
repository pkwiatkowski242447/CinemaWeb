import React, {useEffect, useState} from 'react';
import {Button, Form, Modal, Table} from 'react-bootstrap';
import {api, getAuthToken} from '../api/api.config';
import {MovieType} from "../types/movieType.ts";
import {useFormik} from 'formik';
import * as Yup from 'yup';

interface MoviesTableProps {
}

const MoviesTable: React.FC<MoviesTableProps> = () => {
    const [movies, setMovies] = useState<MovieType[]>([]);
    const [filter, setFilter] = useState('');
    const [selectedMovie, setSelectedMovie] = useState<MovieType | null>(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editedTitle, setEditedTitle] = useState('');
    const [editedBasePrice, setEditedBasePrice] = useState<number>(0);
    const [editedScrRoomNumber, setEditedScrRoomNumber] = useState<number>(0);
    const [editedAvailableSeats, setEditedAvailableSeats] = useState<number>(0);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [movieToDeleteId, setMovieToDeleteId] = useState('');
    const [confirmSave, setConfirmSave] = useState(false);

    useEffect(() => {
        fetchData();
    }, []);

    const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFilter(e.target.value);
    };

    const handleDelete = async (movieId: string) => {
        const hasTickets = await checkTicketsForMovie(movieId);

        if (!hasTickets) {
            setMovieToDeleteId(movieId);
            setShowDeleteModal(true);
        } else {
            alert("Nie można usunąć filmu, ponieważ istnieją związane z nim bilety.");
        }
    };

    const checkTicketsForMovie = async (movieId: string): Promise<boolean> => {
        const endpoint = `/movies/${movieId}/tickets`;

        try {
            const response = await api.get(endpoint);
            const tickets = response.data;
            return tickets.length > 0;
        } catch (error) {
            console.error('Error checking tickets for movie:', error);
            return false;
        }
    };
    const deleteMovie = async (movieId: string) => {
        const endpoint = `/movies/${movieId}/delete`;

        const config = {
            headers: {Authorization: `Bearer ${getAuthToken()}`}
        };

        try {
            const response = await api.delete(endpoint, config);
            fetchData();
        } catch (error) {
            console.error('Error deleting movie:', error);
        }
    };

    const confirmDelete = async () => {
        if (movieToDeleteId) {
            await deleteMovie(movieToDeleteId);
            setMovieToDeleteId('');
            setShowDeleteModal(false);
        }
    };

    const filteredMovies = movies.filter((movie) =>
        movie.movieTitle.toLowerCase().includes(filter.toLowerCase())
    );
    const validationSchema = Yup.object().shape({
        title: Yup.string().required('Tytuł filmu jest wymagany').min(1, 'Tytuł filmu musi zawierać co najmniej 1 znak').max(150, 'Tytuł filmu może zawierać maksymalnie 150 znaków'),
        basePrice: Yup.number().required('Cena bazowa jest wymagana').min(0, 'Cena bazowa musi być co najmniej 0').max(100, 'Cena bazowa nie może przekroczyć 100'),
        scrRoomNumber: Yup.number().required('Numer sali kinowej jest wymagany').min(1, 'Numer sali kinowej musi być co najmniej 1').max(30, 'Numer sali kinowej nie może przekroczyć 30'),
        availableSeats: Yup.number().required('Liczba dostępnych miejsc jest wymagana').min(0, 'Liczba dostępnych miejsc musi być co najmniej 0').max(120, 'Liczba dostępnych miejsc nie może przekroczyć 120'),
    });

    const formik = useFormik({
        initialValues: {
            title: '',
            basePrice: 0,
            scrRoomNumber: 0,
            availableSeats: 0,
        },
        validationSchema,
        onSubmit: async (values) => {
            if (confirmSave) {
                const endpoint = '/movies';

                const movieToSend = {
                    'movie-title': values.title,
                    'movie-base-price': values.basePrice,
                    'scr-room-number': values.scrRoomNumber,
                    'number-of-available-seats': values.availableSeats,
                };

                try {
                    await api.post(endpoint, movieToSend);
                    fetchData();
                    handleCloseCreateModal();
                    setConfirmSave(false);
                    formik.resetForm();
                } catch (error) {
                    console.error('Error creating movie:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    })

    const formikEdit = useFormik({
        initialValues: {
            title: editedTitle || 'AAAAAAAA',
            basePrice: editedBasePrice || 0,
            scrRoomNumber: editedScrRoomNumber || 0,
            availableSeats: editedAvailableSeats || 0,
        },

        validationSchema,
        onSubmit: async (values) => {
            if (confirmSave) {
                const endpoint = '/movies/update';

                const test = await api.get(`/movies/${selectedMovie?.movieId}`)
                const etag = test.headers.etag;

                console.log(etag)

                const movieToSend = {
                    'movie-id': selectedMovie.movieId,
                    'movie-title': values.title,
                    'movie-base-price': values.basePrice,
                    'scr-room-number': values.scrRoomNumber,
                    'number-of-available-seats': values.availableSeats,
                };

                console.log(movieToSend)

                try {
                    await api.put(endpoint, movieToSend, {
                        headers: {
                            'If-Match': etag,
                        },
                    });
                    fetchData();
                    handleCloseEditModal();
                    setConfirmSave(false);
                    formikEdit.resetForm();
                } catch (error) {
                    console.error('Error editing movie:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    });
    const fetchData = async () => {
        try {
            const response = await api.get(`/movies/all`);
            const data = await response.data
            const transformedMovies = data.map((movie: any) => {
                return {
                    movieId: movie["movie-id"],
                    movieTitle: movie["movie-title"],
                    movieBasePrice: movie["movie-base-price"],
                    scrRoomNumber: movie["scr-room-number"],
                    numberOfAvailableSeats: movie["number-of-available-seats"],
                };
            });
            setMovies(transformedMovies);
        } catch (error) {
            console.error('Error fetching movies:', error);
        }
    };

    const handleEdit = (movie: MovieType) => {
        setSelectedMovie(movie);
        setEditedTitle(movie.movieTitle);
        setEditedBasePrice(movie.movieBasePrice);
        setEditedScrRoomNumber(movie.scrRoomNumber);
        setEditedAvailableSeats(movie.numberOfAvailableSeats);
        setShowEditModal(true);
    };

    const handleCreate = () => {
        setEditedTitle('');
        setEditedBasePrice(0);
        setEditedScrRoomNumber(0);
        setEditedAvailableSeats(0);
        setShowCreateModal(true);
    };

    const handleCloseEditModal = () => {
        setSelectedMovie(null);
        setShowEditModal(false);
        setConfirmSave(false);
        formikEdit.resetForm()
    };

    const handleCloseCreateModal = () => {
        setShowCreateModal(false);
        setConfirmSave(false);
        formik.resetForm()
    };

    return (
        <div>
            <Form.Group controlId="filterInput">
                <Form.Label><strong>Filtruj po tytule filmu:</strong> </Form.Label>
                <Form.Control
                    type="text"
                    placeholder="..."
                    value={filter}
                    onChange={handleFilterChange}
                />
            </Form.Group>
            <br/>
            <Table striped bordered responsive hover>
                <thead>
                <tr>
                    <th>ID Filmu</th>
                    <th>Tytuł</th>
                    <th>Cena bazowa</th>
                    <th>Numer sali kinowej</th>
                    <th>Liczba dostępnych miejsc</th>
                    <th>Edytuj</th>
                    <th>Usuń</th>
                </tr>
                </thead>
                <tbody>
                {filteredMovies.map((movie) => (
                    <tr key={movie.movieId}>
                        <td>{movie.movieId}</td>
                        <td>{movie.movieTitle}</td>
                        <td>{movie.movieBasePrice}</td>
                        <td>{movie.scrRoomNumber}</td>
                        <td>{movie.numberOfAvailableSeats}</td>
                        <td>
                            <Button variant="outline-primary" onClick={() => handleEdit(movie)}>
                                Edytuj
                            </Button>
                        </td>
                        <td>
                            <Button variant="outline-danger" onClick={() => handleDelete(movie.movieId)}>
                                Usuń
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
            <Button variant="outline-success" onClick={handleCreate}>
                Dodaj Film
            </Button>

            <Modal show={showEditModal} onHide={handleCloseEditModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Edytuj Film</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={formikEdit.handleSubmit}>
                        <Form.Group controlId="createTitleInput">
                            <Form.Label><strong>Tytuł:</strong></Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Wybierz nowy tytuł filmu"
                                name="title"
                                value={formikEdit.values.title}
                                onChange={formikEdit.handleChange}
                                onBlur={formikEdit.handleBlur}
                                isInvalid={formikEdit.touched.title && !!formikEdit.errors.title}
                                required
                            />
                            <Form.Control.Feedback type="invalid">{formikEdit.errors.title}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createBasePriceInput">
                            <Form.Label>
                                <strong>Cena bazowa:</strong>
                            </Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="..."
                                name="basePrice"
                                value={formikEdit.values.basePrice}
                                onChange={formikEdit.handleChange}
                                onBlur={formikEdit.handleBlur}
                                isInvalid={formikEdit.touched.basePrice && !!formikEdit.errors.basePrice}
                                step="0.01"
                                required
                            />
                            <Form.Control.Feedback type="invalid">{formikEdit.errors.basePrice}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createScrRoomNumberInput">
                            <Form.Label><strong>Numer sali kinowej:</strong></Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="..."
                                name="scrRoomNumber"
                                value={formikEdit.values.scrRoomNumber}
                                onChange={formikEdit.handleChange}
                                onBlur={formikEdit.handleBlur}
                                isInvalid={formikEdit.touched.scrRoomNumber && !!formikEdit.errors.scrRoomNumber}
                                required
                            />
                            <Form.Control.Feedback
                                type="invalid">{formikEdit.errors.scrRoomNumber}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createAvailableSeatsInput">
                            <Form.Label><strong>Liczba dostępnych miejsc:</strong></Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="..."
                                name="availableSeats"
                                value={formikEdit.values.availableSeats}
                                onChange={formikEdit.handleChange}
                                onBlur={formikEdit.handleBlur}
                                isInvalid={formikEdit.touched.availableSeats && !!formikEdit.errors.availableSeats}
                                required
                            />
                            <Form.Control.Feedback
                                type="invalid">{formikEdit.errors.availableSeats}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Button variant="outline-secondary" onClick={handleCloseEditModal}>
                            Anuluj
                        </Button>
                        <Button variant="outline-success" type="submit">
                            Zapisz film
                        </Button>
                        {confirmSave && <div>
                            <br/> <strong>Wciśnij ponownie by potwierdzić.</strong></div>}
                    </Form>
                </Modal.Body>
            </Modal>

            <Modal show={showCreateModal} onHide={handleCloseCreateModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Dodaj Film</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={formik.handleSubmit}>
                        <Form.Group controlId="createTitleInput">
                            <Form.Label><strong>Tytuł:</strong></Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Wpisz tytuł filmu"
                                name="title"
                                value={formik.values.title}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.title && !!formik.errors.title}
                                required
                            />
                            <Form.Control.Feedback type="invalid">{formik.errors.title}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createBasePriceInput">
                            <Form.Label>
                                <strong>Cena bazowa:</strong>
                            </Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="..."
                                name="basePrice"
                                value={formik.values.basePrice}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.basePrice && !!formik.errors.basePrice}
                                step="0.01"
                                required
                            />
                            <Form.Control.Feedback type="invalid">{formik.errors.basePrice}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createScrRoomNumberInput">
                            <Form.Label><strong>Numer sali kinowej:</strong></Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="..."
                                name="scrRoomNumber"
                                value={formik.values.scrRoomNumber}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.scrRoomNumber && !!formik.errors.scrRoomNumber}
                                required
                            />
                            <Form.Control.Feedback type="invalid">{formik.errors.scrRoomNumber}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createAvailableSeatsInput">
                            <Form.Label><strong>Liczba dostępnych miejsc:</strong></Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="..."
                                name="availableSeats"
                                value={formik.values.availableSeats}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.availableSeats && !!formik.errors.availableSeats}
                                required
                            />
                            <Form.Control.Feedback type="invalid">{formik.errors.availableSeats}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Button variant="outline-secondary" onClick={handleCloseCreateModal}>
                            Anuluj
                        </Button>
                        <Button variant="outline-success" type="submit">
                            Zapisz film
                        </Button>
                        {confirmSave && <div>
                            <br/> <strong>Wciśnij ponownie by potwierdzić.</strong></div>}
                    </Form>
                </Modal.Body>
            </Modal>

            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Usuń Film</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Czy na pewno chcesz usunąć film?
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="outline-secondary" onClick={() => setShowDeleteModal(false)}>
                        Anuluj
                    </Button>
                    <Button variant="outline-danger" onClick={confirmDelete}>
                        Usuń
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default MoviesTable;
