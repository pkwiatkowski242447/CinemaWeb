import React, {useEffect, useState} from 'react';
import {Button, Form, Modal, Table} from 'react-bootstrap';
import {api} from '../api/api.config';
import {TicketType} from '../types/ticketType.ts';
import {AccountType} from '../types/accountType.ts';
import {MovieType} from '../types/movieType.ts';
import {useFormik} from 'formik';
import * as Yup from 'yup';


interface TicketsTableProps {
}

const AllTicketsTable: React.FC<TicketsTableProps> = () => {
    const [tickets, setTickets] = useState<TicketType[]>([]);
    const [clients, setClients] = useState<AccountType[]>([]);
    const [movies, setMovies] = useState<MovieType[]>([]);
    const [availableMovies, setAvailableMovies] = useState<MovieType[]>([]);
    const [activeClients, setActiveClients] = useState<AccountType[]>([]);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editedMovieTime, setEditedMovieTime] = useState<string>('');
    const [editedTicketFinalPrice, setEditedTicketFinalPrice] = useState<number>(0);
    const [editedClientId, setEditedClientId] = useState<AccountType['id']>('');
    const [editedMovieId, setEditedMovieId] = useState<MovieType['movieId']>('');
    const [confirmSave, setConfirmSave] = useState(false);
    const [selectedClientId, setSelectedClientId] = useState('');

    const validationSchema = Yup.object().shape({
        movieTime: Yup.string().required('Czas Seansu jest wymagany').matches(/^\d{4}-(0[1-9]|1[0-2])-([0-2]\d|3[01])T(?:[01]\d|2[0-3]):[0-5]\d:[0-5]\d$/, 'Nieprawidłowy format czasu'),
        clientId: Yup.string().required('Klient jest wymagany'),
        movieId: Yup.string().required('Tytuł filmu jest wymagany'),
    });

    useEffect(() => {
        fetchData();
    }, [clients, movies, selectedClientId]);

    const filterMovies = () => {
        const filteredMovies = movies.filter(movie => movie.numberOfAvailableSeats > 0);
        setAvailableMovies(filteredMovies);
    }

    useEffect(() => {
        filterMovies()
    }, [movies]);

    const filterClients = () => {
        const filteredClients = clients.filter(client => client.statusActive);
        setActiveClients(filteredClients);
    }

    useEffect(() => {
        filterClients()
    }, [clients]);

    const getAllMovies = async () => {
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

    useEffect(() => {
        getAllMovies()
    }, []);

    const fetchClients = async () => {
        try {
            const response = await api.get(`/clients/all`);
            const data = await response.data
            const transformedClients = data.map((client: any) => {
                return {
                    id: client.userID,
                    login: client.userLogin,
                    statusActive: client.userStatusActive,
                };
            });
            setClients(transformedClients);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    useEffect(() => {
        fetchClients()
    }, []);

    const fetchData = async () => {
        try {
            const ticketsEndpoint = selectedClientId
                ? `/clients/${selectedClientId}/ticket-list`
                : '/tickets/all';

            const response = await api.get(ticketsEndpoint);
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
                    clientId: ticket['client-id'],
                    movieId: ticket['movie-id'],
                };
            });

            const updatedTickets = await Promise.all(
                transformedTickets.map(async (ticket) => {
                    const client = clients.find((c) => c.id === ticket.clientId);
                    const movie = movies.find((m) => m.movieId === ticket.movieId);
                    return {
                        ...ticket,
                        clientId: client ? client.login : 'Unknown Client',
                        movieId: movie ? movie.movieTitle : 'Unknown Movie',
                    };
                })
            );

            setTickets(updatedTickets);
        } catch (error) {
            console.error('Error fetching tickets:', error);
        }
    };

    const handleCreate = () => {
        setEditedMovieTime('');
        setEditedTicketFinalPrice(0);
        setEditedClientId('');
        setEditedMovieId('');
        setShowCreateModal(true);
    };

    const formik = useFormik({
        initialValues: {
            movieTime: '',
            clientId: '',
            movieId: '',
        },
        validationSchema,
        onSubmit: async (values) => {
            const endpoint = '/tickets';

            const ticketToSend = {
                'movie-time': values.movieTime,
                'client-id': values.clientId,
                'movie-id': values.movieId,
            };

            if (confirmSave) {
                try {
                    const response = await api.post(endpoint, ticketToSend);
                    if (response == null || response == undefined) {
                        alert("Wystąpił problem z kupnem biletu na ten film - film jest już niedostępny i/lub klient został dezaktywowany.")
                        await getAllMovies()
                        await fetchClients()
                        filterMovies()
                        filterClients()
                        setConfirmSave(false);
                    } else {
                        fetchData();
                        handleCloseCreateModal();
                        setConfirmSave(false);
                        formik.resetForm();
                    }
                } catch (error) {
                    console.error('Error creating ticket:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    });

    const handleCloseCreateModal = () => {
        setShowCreateModal(false);
        formik.resetForm();
        setConfirmSave(false);
    };

    return (
        <div>
            <Form.Group controlId="clientLoginFilter">
                <Form.Label><strong>Filtruj po loginie klienta:</strong></Form.Label>
                <Form.Control
                    as="select"
                    value={selectedClientId}
                    onChange={(e) => setSelectedClientId(e.target.value)}
                >
                    <option value="">Wszyscy klienci</option>
                    {clients.map((client) => (
                        <option key={client.id} value={client.id}>
                            {client.login}
                        </option>
                    ))}
                </Form.Control>
            </Form.Group>
            <br/>
            {tickets.length === 0 ? (
                <strong> Nie znaleziono biletów dla wybranego użytkownika <br/></strong>
            ) : (
                <Table striped bordered responsive hover>
                    <thead>
                    <tr>
                        <th>ID Biletu</th>
                        <th>Godzina Seansu</th>
                        <th>Cena Biletu</th>
                        <th>Login Klienta</th>
                        <th>Tytuł Filmu</th>
                    </tr>
                    </thead>
                    <tbody>
                    {tickets.map((ticket) => (
                        <tr key={ticket.ticketId}>
                            <td>{ticket.ticketId}</td>
                            <td>{ticket.movieTime}</td>
                            <td>{ticket.ticketFinalPrice}</td>
                            <td>{ticket.clientId}</td>
                            <td>{ticket.movieId}</td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            )}
            <Button variant="outline-success" onClick={handleCreate}>
                Utwórz Bilet
            </Button>

            <Modal show={showCreateModal} onHide={handleCloseCreateModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Utwórz Bilet</Modal.Title>
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
                        <Form.Group controlId="createClientIdInput">
                            <Form.Label>
                                <strong>Klient:</strong>
                            </Form.Label>
                            <Form.Select
                                name="clientId"
                                value={formik.values.clientId}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.clientId && !!formik.errors.clientId}
                                required
                            >
                                <option value="">Wybierz klienta</option>
                                {activeClients.map((client) => (
                                    <option key={client.id} value={client.id}>
                                        {client.login}
                                    </option>
                                ))}
                            </Form.Select>
                            <Form.Control.Feedback type="invalid">{formik.errors.clientId}</Form.Control.Feedback>
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
        </div>
    )
        ;
};

export default AllTicketsTable;
