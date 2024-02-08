import React from "react";
import { FC } from "react";
import { useNavigate } from "react-router-dom";

const LoginButton: FC = () => {
    const navigation = useNavigate();

    const handleClick = () => {
        navigation("/login")
    }

    return (
        <button
            className="login-button"
            style={{ fontWeight: "bold", color: "white" }}
            onClick={handleClick}
        >
            Zaloguj się
        </button>
    );
}

export default LoginButton;
