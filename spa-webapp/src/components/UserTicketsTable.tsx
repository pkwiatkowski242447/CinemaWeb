import React, {useEffect, useState} from 'react';
import {Button, Form, Modal, Table} from 'react-bootstrap';
import {api, getAuthToken} from '../api/api.config';
import {TicketType} from '../types/ticketType.ts';
import {MovieType} from '../types/movieType.ts';
import {useFormik} from 'formik';
import * as Yup from 'yup';

interface TicketsTableProps {

}

const UserTicketsTable: React.FC<TicketsTableProps> = () => {
    const [tickets, setTickets] = useState<TicketType[]>([]);
    const [movies, setMovies] = useState<MovieType[]>([]);
    const [availableMovies, setAvailableMovies] = useState<MovieType[]>([]);
    const [selectedTicket, setSelectedTicket] = useState<TicketType | null>(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editedMovieTime, setEditedMovieTime] = useState<string>('');
    const [editedTicketFinalPrice, setEditedTicketFinalPrice] = useState<number>(0);
    const [editedMovieId, setEditedMovieId] = useState<MovieType['movieId']>('');
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [ticketToDeleteId, setTicketToDeleteId] = useState('');
    const [confirmSave, setConfirmSave] = useState(false);

    const getUserID = async () => {
        const test = await api.get('/clients/login/self')
        return test.data.userID;
    }

    const validationSchema = Yup.object().shape({
        movieTime: Yup.string().required('Czas Seansu jest wymagany').matches(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/, 'Nieprawidłowy format czasu'),
        movieId: Yup.string().required('Tytuł filmu jest wymagany'),
    });

    useEffect(() => {
        fetchData();
    }, [movies]);


    useEffect(() => {
        const filteredMovies = movies.filter(movie => movie.numberOfAvailableSeats > 0);
        setAvailableMovies(filteredMovies);
    }, [movies]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await api.get('/movies/all');
                const data = await response.data;

                const transformedMovies = data.map((movie: MovieType) => {
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

        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const ticketsEndpoint = `/clients/self/ticket-list`
            const config = {
                headers: {Authorization: `Bearer ${getAuthToken()}`}
            };

            const response = await api.get(ticketsEndpoint, config);
            const data = await response.data;

            if (!Array.isArray(data)) {
                console.error('Invalid data format:', data);
                const emptyArr = [];
                setTickets(emptyArr);
                return;
            }

            const transformedTickets = data.map((ticket: TicketType) => {
                return {
                    ticketId: ticket['ticket-id'],
                    movieTime: ticket['movie-time'],
                    ticketFinalPrice: ticket['ticket-final-price'],
                    movieId: ticket['movie-id'],
                };
            });

            const updatedTickets = await Promise.all(
                transformedTickets.map(async (ticket) => {
                    const movie = movies.find((m) => m.movieId === ticket.movieId);
                    return {
                        ...ticket,
                        movieId: movie ? movie.movieTitle : 'Unknown Movie',
                    };
                })
            );

            setTickets(updatedTickets);
        } catch (error) {
            console.error('Error fetching tickets:', error);
        }
    };


    const deleteTicket = async (ticketId: string) => {
        const endpoint = `/tickets/${ticketId}/delete`;
        const config = {
            headers: {Authorization: `Bearer ${getAuthToken()}`}
        };

        try {
            await api.delete(endpoint, config);
            fetchData();
        } catch (error) {
            console.error('Error deleting movie:', error);
        }
    };

    const handleDelete = (ticketId: string) => {
        setTicketToDeleteId(ticketId);
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        if (ticketToDeleteId) {
            await deleteTicket(ticketToDeleteId);
            setTicketToDeleteId('');
            setShowDeleteModal(false);
        }
    };

    const handleEdit = (ticket: TicketType) => {
        setSelectedTicket(ticket);
        setEditedMovieTime(ticket.movieTime);
        setEditedTicketFinalPrice(ticket.ticketFinalPrice);
        setEditedMovieId(ticket.movieId);
        setShowEditModal(true);
    };

    const handleCreate = () => {
        setEditedMovieTime('');
        setEditedTicketFinalPrice(0);
        setEditedMovieId('');
        setShowCreateModal(true);
    };

    const formik = useFormik({
        initialValues: {
            movieTime: '',
            movieId: '',
        },
        validationSchema,
        onSubmit: async (values) => {
            const endpoint = '/tickets/self';
            const ticketToSend = {
                'movie-time': values.movieTime,
                'client-id': getUserID(),
                'movie-id': values.movieId,
            };

            if (confirmSave) {
                try {
                    await api.post(endpoint, ticketToSend);
                    fetchData();
                    handleCloseCreateModal();
                    setConfirmSave(false);
                    formik.resetForm();
                } catch (error) {
                    console.error('Error creating ticket:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    });

    const formikEdit = useFormik({
        initialValues: {
            movieTime: selectedTicket?.movieTime,
        },
        validationSchema: Yup.object().shape({
            movieTime: Yup.string()
                .required('Czas Seansu jest wymagany')
                .matches(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/, 'Nieprawidłowy format czasu'),
        }),
        onSubmit: async (values) => {
            const endpoint = `/tickets`;

            const ticketToSend = {
                'ticket-id': selectedTicket.ticketId,
                'movie-time': values.movieTime,
            };

            if (confirmSave) {
                try {
                    await api.put(endpoint, ticketToSend);
                    fetchData();
                    handleCloseEditModal();
                    setConfirmSave(false);
                } catch (error) {
                    console.error('Error saving edit:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    });

    const handleCloseEditModal = () => {
        setSelectedTicket(null);
        setShowEditModal(false);
        formikEdit.resetForm()
        setConfirmSave(false);
    };

    const handleCloseCreateModal = () => {
        setShowCreateModal(false);
        formik.resetForm();
        setConfirmSave(false);
    };

    return (
        <div>
            <br/>
            {tickets.length === 0 ? (
                <strong> Nie posiadasz żadnych biletów.<br/></strong>
            ) : (
                <Table striped bordered responsive hover>
                    <thead>
                    <tr>
                        <th>ID Biletu</th>
                        <th>Godzina Seansu</th>
                        <th>Cena Biletu</th>
                        <th>Tytuł Filmu</th>
                        <th>Edytuj</th>
                        <th>Usuń</th>
                    </tr>
                    </thead>
                    <tbody>
                    {tickets.map((ticket) => (
                        <tr key={ticket.ticketId}>
                            <td>{ticket.ticketId}</td>
                            <td>{ticket.movieTime}</td>
                            <td>{ticket.ticketFinalPrice}</td>
                            <td>{ticket.movieId}</td>
                            <td>
                                <Button variant="outline-primary" onClick={() => handleEdit(ticket)}>
                                    Edytuj
                                </Button>
                            </td>
                            <td>
                                <Button variant="outline-danger" onClick={() => handleDelete(ticket.ticketId)}>
                                    Usuń
                                </Button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            )}
            <Button variant="outline-success" onClick={handleCreate}>
                Kup Bilet
            </Button>
            <Modal show={showEditModal} onHide={handleCloseEditModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Edytuj Bilet</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={formikEdit.handleSubmit}>
                        <Form.Group controlId="editMovieTimeInput">
                            <Form.Label>Czas Seansu:</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Wpisz czas seansu:"
                                name="movieTime"
                                value={formikEdit.values.movieTime}
                                onChange={formikEdit.handleChange}
                                onBlur={formikEdit.handleBlur}
                                isInvalid={formikEdit.touched.movieTime && !!formikEdit.errors.movieTime}
                            />
                            <Form.Control.Feedback type="invalid">{formikEdit.errors.movieTime}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="editTicketFinalPriceInput">
                            <Form.Label>Cena Biletu:</Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="Wpisz cenę biletu"
                                step="0.01"
                                value={editedTicketFinalPrice}
                                onChange={(e) => setEditedTicketFinalPrice(Number(e.target.value))}
                                disabled
                            />
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="editMovieIdInput">
                            <Form.Label>Tytuł Filmu:</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Enter movie ID"
                                value={editedMovieId}
                                onChange={(e) => setEditedMovieId(e.target.value)}
                                disabled
                            />
                        </Form.Group>
                        <br/>
                        <Button variant="outline-secondary" onClick={handleCloseEditModal}>
                            Anuluj
                        </Button>
                        <Button variant="outline-primary" type="submit">
                            Zapisz zmiany
                        </Button>
                        {confirmSave && <div>
                            <br/> <strong>Wciśnij ponownie by potwierdzić.</strong></div>}
                    </Form>
                </Modal.Body>
            </Modal>

            <Modal show={showCreateModal} onHide={handleCloseCreateModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Kup Bilet</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={formik.handleSubmit}>
                        <Form.Group controlId="createMovieTimeInput">
                            <Form.Label>
                                <strong>Czas Seansu:</strong>
                            </Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Wpisz czas seansu"
                                name="movieTime"
                                value={formik.values.movieTime}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.movieTime && !!formik.errors.movieTime}
                                required
                            />
                            <Form.Control.Feedback type="invalid">{formik.errors.movieTime}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createMovieIdInput">
                            <Form.Label>
                                <strong>Tytuł filmu:</strong>
                            </Form.Label>
                            <Form.Select
                                name="movieId"
                                value={formik.values.movieId}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.movieId && !!formik.errors.movieId}
                                required
                            >
                                <option value="">Wybierz film</option>
                                {availableMovies.map((movie) => (
                                    <option key={movie.movieId} value={movie.movieId}>
                                        {movie.movieTitle}
                                    </option>
                                ))}
                            </Form.Select>
                            <Form.Control.Feedback type="invalid">{formik.errors.movieId}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Button variant="outline-secondary" onClick={handleCloseCreateModal}>
                            Anuluj
                        </Button>
                        <Button variant="outline-success" type="submit">
                            Zapisz bilet
                        </Button>
                        {confirmSave && <div>
                            <br/> <strong>Wciśnij ponownie by potwierdzić.</strong></div>}
                    </Form>
                </Modal.Body>
            </Modal>

            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Usuń Bilet</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Czy na pewno chcesz usunąć bilet?
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
    )
        ;
};

export default UserTicketsTable;
