import {FC, useEffect} from "react";
import {Outlet, useNavigate} from "react-router-dom";

const AuthenticationLayout: FC = () => {

    const token = window.localStorage.getItem("token");
    const navigation = useNavigate();

    useEffect(() => {
        if (token != null && token !== "null") {
            navigation("/");
        }
    }, [token, navigation]);

    return (
        <main className="h-screen flex justify-center items-center">
            <div className="flex w-[90vw] h-[90vh] shadow-large-shadow bg-gray-800">
                <div className="bg-white h-[100%] w-[100%]">
                    <Outlet/>
                </div>
            </div>
        </main>
    );
}

export default AuthenticationLayout;