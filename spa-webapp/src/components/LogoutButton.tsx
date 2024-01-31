import { FC } from "react";
import { useNavigate } from "react-router-dom";
import { RiLogoutBoxLine } from "react-icons/ri";

interface LogoutButtonProps {
    logoutUser: () => void;
}

const LogoutButton: FC<LogoutButtonProps> = ({logoutUser}) => {
    const navigation = useNavigate();

    const handleLogout = () => {
        logoutUser();
        navigation("/login");
    };

    return (
        <div className="logout-button-container">
            <button onClick={handleLogout}>
                <RiLogoutBoxLine className="logout-icon h-10 w-10 text-white">
                    Wyloguj się
                </RiLogoutBoxLine>
            </button>
        </div>

    );
};

export default LogoutButton;