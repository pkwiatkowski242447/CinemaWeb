import React, {useEffect, useState} from 'react';
import {Button, Form, Modal, Table} from 'react-bootstrap';
import {AccountType} from "../types/accountType.ts";
import {api} from "../api/api.config.ts";
import {useFormik} from 'formik';
import * as Yup from 'yup';
import {useUserContext} from "../context/userContext.tsx";

interface TableProps {
    role: 'clients' | 'admins' | 'staffs';
}

const UsersTable: React.FC<TableProps> = ({role}) => {
    const [users, setUsers] = useState<AccountType[]>([]);
    const [filter, setFilter] = useState('');
    const [selectedUser, setSelectedUser] = useState<AccountType | null>(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editedPassword, setEditedPassword] = useState('');
    const [editedLogin, setEditedLogin] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [confirmSave, setConfirmSave] = useState(false);
    const {account} = useUserContext();

    let modalTitle = '';
    let filterText = '';

    switch (role) {
        case 'clients':
            modalTitle = 'Dodaj Klienta';
            filterText = 'klienta:';
            break;
        case 'admins':
            modalTitle = 'Dodaj Admina';
            filterText = 'admina:';
            break;
        case 'staffs':
            modalTitle = 'Dodaj Pracownika';
            filterText = 'pracownika:';
            break;
        default:
            modalTitle = 'Dodaj Użytkownika';
            filterText = 'użytkownika:';
            break;
    }

    const filteredUsers = users.filter((user) =>
        user.login.toLowerCase().includes(filter.toLowerCase())
    );

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const response = await api.get(`/${role}/all`);
            const data = await response.data

            const transformedUsers = data.map((user: any) => {
                return {
                    id: user.userID,
                    login: user.userLogin,
                    statusActive: user.userStatusActive
                };
            });
            setUsers(transformedUsers);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    const handleEdit = (user: AccountType) => {
        setSelectedUser(user);
        setEditedPassword('');
        setConfirmPassword('');
        setShowEditModal(true);
    };

    const handleCreate = () => {
        setEditedLogin('');
        setEditedPassword('');
        setConfirmPassword('');
        setShowCreateModal(true);
    };

    const validationSchema = Yup.object().shape({
        login: Yup.string()
            .required('Login jest wymagany')
            .min(8, 'Login musi mieć co najmniej 8 znaków')
            .max(20, 'Login może mieć maksymalnie 20 znaków')
            .notOneOf(users.map(user => user.login), 'Login już istnieje'),
        password: Yup.string()
            .required('Hasło jest wymagane')
            .min(8, 'Hasło musi mieć co najmniej 8 znaków')
            .max(40, 'Hasło może mieć maksymalnie 40 znaków'),
        confirmPassword: Yup.string()
            .required('Potwierdzenie hasła jest wymagane')
            .oneOf([Yup.ref('password'), null], 'Hasła muszą być identyczne'),
    });

    const validationSchemaEdit = Yup.object().shape({
        password: Yup.string()
            .required('Hasło jest wymagane')
            .min(8, 'Hasło musi mieć co najmniej 8 znaków')
            .max(40, 'Hasło może mieć maksymalnie 40 znaków'),
        confirmPassword: Yup.string()
            .required('Potwierdzenie hasła jest wymagane')
            .oneOf([Yup.ref('password'), null], 'Hasła muszą być identyczne'),
    });

    const formik = useFormik({
        initialValues: {
            login: '',
            password: '',
            confirmPassword: '',
        },
        validationSchema,
        onSubmit: async (values) => {
            if (editedPassword !== confirmPassword) {
                console.error('Passwords do not match');
                return;
            }

            const saveToSend = {
                userLogin: values.login,
                userPassword: values.password
            }
            const endpoint = `/auth/register/${role.slice(0, -1)}`;

            if (confirmSave) {
                try {
                    await api.post(endpoint, saveToSend);
                    fetchData();
                    handleCloseCreateModal();
                    setConfirmSave(false);
                    formik.resetForm()
                } catch (error) {
                    console.error('Error creating user:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    });

    const formikEdit = useFormik({
        initialValues: {
            password: '',
            confirmPassword: '',
        },
        validationSchemaEdit,
        onSubmit: async (values) => {
            if (editedPassword !== confirmPassword) {
                console.error('Passwords do not match');
                return;
            }

            const editToSend = {
                userID: selectedUser?.id,
                userLogin: selectedUser?.login,
                userPassword: values.password,
                userStatusActive: selectedUser?.statusActive
            }

            const endpoint = `/${role}/update`;
            console.log(editToSend)
            if (confirmSave) {
                try {
                    const test = await api.get('/admins/login/self')
                    const etag = test.headers.etag;
                    await api.put(endpoint, editToSend, {
                        headers: {
                            'If-Match': etag,
                        },
                    });
                    fetchData();
                    handleCloseEditModal();
                    setConfirmSave(false);
                    formik.resetForm()
                } catch (error) {
                    console.error('Error creating user:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    });

    const handleToggleStatus = async (userId: string, activate: boolean) => {
        const endpoint = activate
            ? `/${role}/${userId}/activate`
            : `/${role}/${userId}/deactivate`;

        try {
            await api.post(endpoint)
            fetchData()
        } catch (error) {
            console.error('Error toggling status:', error);
        }
    };

    const handleCloseEditModal = () => {
        setShowEditModal(false);
        setConfirmSave(false);
        formikEdit.resetForm();
    };

    const handleCloseCreateModal = () => {
        setShowCreateModal(false);
        setConfirmSave(false);
        formik.resetForm();
    };

    return (
        <div>
            <Form.Group controlId="filterInput">
                <Form.Label><strong>Filtruj po loginie {filterText}</strong></Form.Label>
                <Form.Control
                    type="text"
                    placeholder="..."
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                />
            </Form.Group>
            <br/>
            <Table striped responsive bordered hover>
                <thead>
                <tr>
                    <th>ID Użytkownika</th>
                    <th>Login</th>
                    <th>Status</th>
                    <th>Akcja</th>
                    <th>Edytuj</th>
                </tr>
                </thead>
                <tbody>
                {filteredUsers.map((user) => (
                    <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.login}</td>
                        <td>{user.statusActive ? 'Aktywny' : 'Nieaktywny'}</td>
                        <td>
                            <Button
                                variant={user.statusActive ? 'outline-danger' : 'outline-success'}
                                onClick={() => handleToggleStatus(user.id, !user.statusActive)}
                                disabled={account.login == user.login}
                            >
                                {user.statusActive ? 'Dezaktywuj' : 'Aktywuj'}
                            </Button>
                        </td>
                        <td>
                            <Button variant="outline-primary" onClick={() => handleEdit(user)}>
                                Edytuj
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>

            <Button variant="outline-success" onClick={handleCreate}>
                {modalTitle}
            </Button>

            <Modal show={showEditModal} onHide={handleCloseEditModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Edytuj użytkownika {selectedUser?.login}:</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={formikEdit.handleSubmit}>
                        {/*<Form.Group controlId="createLoginInput">*/}
                        {/*    <Form.Label><strong>Login:</strong></Form.Label>*/}
                        {/*    <Form.Control*/}
                        {/*        type="text"*/}
                        {/*        placeholder="Wpisz nowy login"*/}
                        {/*        name="login"*/}
                        {/*        value={formikEdit.values.login}*/}
                        {/*        onChange={formikEdit.handleChange}*/}
                        {/*        onBlur={formikEdit.handleBlur}*/}
                        {/*        isInvalid={formikEdit.touched.login && !!formikEdit.errors.login}*/}
                        {/*    />*/}
                        {/*    <Form.Control.Feedback type="invalid">{formikEdit.errors.login}</Form.Control.Feedback>*/}
                        {/*</Form.Group>*/}
                        <br/>
                        <Form.Group controlId="createPasswordInput">
                            <Form.Label><strong>Hasło:</strong></Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Wpisz nowe hasło"
                                name="password"
                                value={formikEdit.values.password}
                                onChange={formikEdit.handleChange}
                                onBlur={formikEdit.handleBlur}
                                isInvalid={formikEdit.touched.password && !!formikEdit.errors.password}
                            />
                            <Form.Control.Feedback type="invalid">{formikEdit.errors.password}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="confirmCreatePasswordInput">
                            <Form.Label><strong>Potwierdź hasło:</strong></Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Powtórz hasło"
                                name="confirmPassword"
                                value={formikEdit.values.confirmPassword}
                                onChange={formikEdit.handleChange}
                                onBlur={formikEdit.handleBlur}
                                isInvalid={formikEdit.touched.confirmPassword && !!formikEdit.errors.confirmPassword}
                            />
                            <Form.Control.Feedback
                                type="invalid">{formikEdit.errors.confirmPassword}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Button variant="outline-secondary" onClick={handleCloseEditModal}>
                            Anuluj
                        </Button>
                        <Button variant="outline-success" type="submit">
                            Zapisz zmiany
                        </Button>
                        {confirmSave && <div>
                            <br/> <strong>Wciśnij ponownie by potwierdzić.</strong></div>}
                    </Form>
                </Modal.Body>
            </Modal>

            <Modal show={showCreateModal} onHide={handleCloseCreateModal}>
                <Modal.Header closeButton>
                    <Modal.Title>{modalTitle}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={formik.handleSubmit}>
                        <Form.Group controlId="createLoginInput">
                            <Form.Label><strong>Login:</strong></Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Wpisz login"
                                name="login"
                                value={formik.values.login}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.login && !!formik.errors.login}
                            />
                            <Form.Control.Feedback type="invalid">{formik.errors.login}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="createPasswordInput">
                            <Form.Label><strong>Hasło:</strong></Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Wpisz hasło"
                                name="password"
                                value={formik.values.password}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.password && !!formik.errors.password}
                            />
                            <Form.Control.Feedback type="invalid">{formik.errors.password}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Form.Group controlId="confirmCreatePasswordInput">
                            <Form.Label><strong>Potwierdź hasło:</strong></Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="Powtórz hasło"
                                name="confirmPassword"
                                value={formik.values.confirmPassword}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                                isInvalid={formik.touched.confirmPassword && !!formik.errors.confirmPassword}
                            />
                            <Form.Control.Feedback
                                type="invalid">{formik.errors.confirmPassword}</Form.Control.Feedback>
                        </Form.Group>
                        <br/>
                        <Button variant="outline-secondary" onClick={handleCloseCreateModal}>
                            Anuluj
                        </Button>
                        <Button variant="outline-success" type="submit">
                            Zapisz zmiany
                        </Button>
                        {confirmSave && <div>
                            <br/> <strong>Wciśnij ponownie by potwierdzić.</strong></div>}
                    </Form>
                </Modal.Body>
            </Modal>
        </div>

    );
};

export default UsersTable;