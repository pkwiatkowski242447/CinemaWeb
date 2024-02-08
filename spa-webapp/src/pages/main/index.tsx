import React, {FC, useState} from "react";
import {useUserContext} from "../../context/userContext.tsx";
import {Button, Form, Modal} from "react-bootstrap";
import * as Yup from "yup";
import {useFormik} from "formik";
import {api} from "../../api/api.config.ts";

const MainPage: FC = () => {
    const [showEditModal, setShowEditModal] = useState(false);
    const [editedPassword, setEditedPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [confirmSave, setConfirmSave] = useState(false);
    const {account} = useUserContext();

    const validationSchema = Yup.object().shape({
        password: Yup.string()
            .required('Hasło jest wymagane')
            .min(8, 'Hasło musi mieć co najmniej 8 znaków')
            .max(40, 'Hasło może mieć maksymalnie 40 znaków'),
        confirmPassword: Yup.string()
            .required('Potwierdzenie hasła jest wymagane')
            .oneOf([Yup.ref('password'), null], 'Hasła muszą być identyczne'),
    });

    const formikEdit = useFormik({
        initialValues: {
            password: '',
            confirmPassword: '',
        },
        validationSchema,
        onSubmit: async (values) => {
            if (editedPassword !== confirmPassword) {
                console.error('Passwords do not match');
                return;
            }

            const endpoint = `/${account?.role[0]?.toLowerCase()}s/update`;

            if (confirmSave) {
                try {
                    const test = await api.get(`/${account?.role[0]?.toLowerCase()}s/login/self`)
                    const etag = test.headers.etag;
                    const editToSend = {
                        userID: test.data.userID,
                        userLogin: test.data.userLogin,
                        userPassword: values.password,
                        userStatusActive: test.data.userStatusActive
                    }

                    await api.put(endpoint, editToSend, {
                        headers: {
                            'If-Match': etag,
                        },
                    });
                    handleCloseEditModal();
                    setConfirmSave(false);
                    formikEdit.resetForm()
                    alert("Hasło zostało zmienione poprawnie.")
                } catch (error) {
                    console.error('Error creating user:', error);
                }
            } else {
                setConfirmSave(true);
            }
        },
    });

    const handleEdit = () => {
        setEditedPassword('');
        setConfirmPassword('');
        setShowEditModal(true);
    };

    const handleCloseEditModal = () => {
        setShowEditModal(false);
        setConfirmSave(false);
        formikEdit.resetForm();
    };

    return (
        <>
            <div className="text-center">
                {account?.login && (
                    <>
                        <br/>
                        <p className="h2">Witaj {account.login}.</p>
                        <p className="h2">Twoja rola: {account.role[0]}</p>
                        <br/>
                        <Button variant="outline-danger" onClick={() => handleEdit()}>
                            Zmień hasło
                        </Button>
                    </>
                )}
            </div>
            <div>
                <Modal show={showEditModal} onHide={handleCloseEditModal}>
                    <Modal.Header closeButton>
                        <Modal.Title>Zmień hasło:</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form onSubmit={formikEdit.handleSubmit}>
                            <Form.Group controlId="createPasswordInput">
                                <Form.Label><strong>Hasło:</strong></Form.Label>
                                <Form.Control
                                    type="password"
                                    placeholder="Wpisz nowe hasło"
                                    name="password"
                                    value={formikEdit.values.password}
                                    onChange={formikEdit.handleChange}
                                    onBlur={formikEdit.handleBlur}
                                    isInvalid={formikEdit.touched.password && !!formikEdit.errors.password}/>
                                <Form.Control.Feedback
                                    type="invalid">{formikEdit.errors.password}</Form.Control.Feedback>
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
                                    isInvalid={formikEdit.touched.confirmPassword && !!formikEdit.errors.confirmPassword}/>
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
            </div>
        </>
    );
};

export default MainPage;
