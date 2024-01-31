import {Form, Modal} from "react-bootstrap";
import React from "react";

<Modal show={showEditModal} onHide={handleCloseEditModal}>
    <Modal.Header closeButton>
        <Modal.Title>Edytuj użytkownika {selectedUser?.login}:</Modal.Title>
    </Modal.Header>
    <Modal.Body>
        <Form.Group controlId="editLoginInput">
            <Form.Label><strong>Login:</strong></Form.Label>
            <Form.Control
                type="text"
                value={editedLogin}
                placeholder="Wpisz nowy login"
                onChange={(e) => setEditedLogin(e.target.value)}
            />
        </Form.Group>
        <br/>
        <Form.Group controlId="editedPasswordInput">
            <Form.Label><strong>Hasło:</strong></Form.Label>
            <Form.Control
                type="text"
                placeholder="Wpisz nowe hasło"
                value={editedPassword}
                onChange={(e) => setEditedPassword(e.target.value)}
            />
        </Form.Group>
        <br/>
        <Form.Group controlId="confirmPasswordInput">
            <Form.Label><strong>Potwierdź hasło:</strong></Form.Label>
            <Form.Control
                type="text"
                placeholder="Powtórz hasło"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
            />
        </Form.Group>
    </Modal.Body>
</Modal>