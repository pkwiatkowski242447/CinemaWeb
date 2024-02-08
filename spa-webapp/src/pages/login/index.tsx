import React, {FC, useState} from "react";
import {FormProvider, useForm} from "react-hook-form";
import Input from "../../components/fields/Input";
import Button from "../../components/Button";
import {useNavigate} from "react-router-dom";
import {jwtDecode} from "jwt-decode";
import {AccountType, AccountTypeEnum} from "../../types/accountType.ts";
import {LoginRequest} from "../../types/loginRequest.ts";
import {useUserContext} from "../../context/userContext.tsx";
import {api, setAuthHeader} from "../../api/api.config.ts";
import logo2 from "../assets/logo2.gif";

interface DecodedToken {
    userInformation: {
        login: string;
        user_role: AccountType;
    };
    role: AccountTypeEnum;
}

const LoginPage: FC = () => {
    const methods = useForm<LoginRequest>({
        values: {
            userLogin: "",
            userPassword: "",
        },
    });
    const {setAccount} = useUserContext();
    const {handleSubmit} = methods;
    const navigation = useNavigate();
    const [selectedUserType, setSelectedUserType] = useState<string>("client");

    const onSubmit = handleSubmit((values) => {
        const endpoint = `/auth/login/${selectedUserType}`;

        api.post(endpoint, values)
            .then((response) => {
                if (response == null || response == undefined) {
                    alert("Podano nieprawidłowe dane logowania.")
                }

                const receivedToken = response.data;
                setAuthHeader(receivedToken);
                const alreadyDecodedToken: DecodedToken = jwtDecode(receivedToken);
                setAccount({
                    login: alreadyDecodedToken.sub,
                    role: alreadyDecodedToken.user_role,
                });
                navigation("/");
            })
            .catch((error) => {
                console.error(error);
            });
    });

    const handleClick = () => {
        navigation("/register");
    };

    return (
        <>
            <br/><br/><br/>
            <div className="flex justify-center">
                <img src={logo2} alt="Loading GIF"/>
            </div>
            <p className="font-sans text-2xl font-bold mt-4 text-center text-gray-800">Logowanie</p>
            <div className="flex justify-center">
                <FormProvider {...methods}>
                    <form onSubmit={onSubmit} className="pt-[50px] w-[60%]">
                        <div>
                            <label htmlFor="userType"><strong> Wybierz typ użytkownika: </strong> </label>
                            <select
                                id="userType"
                                name="userType"
                                value={selectedUserType}
                                onChange={(e) => setSelectedUserType(e.target.value)}
                            >
                                <option value="admin">Admin</option>
                                <option value="client">Client</option>
                                <option value="staff">Staff</option>
                            </select>
                        </div>
                        <br/>
                        <Input label="Login: " placeholder="..." name="userLogin"/>
                        <Input label="Hasło: " placeholder="..." name="userPassword" type="password"/>
                        <div onClick={handleClick} className="hover:cursor-pointer">
                            <strong>Zarejestruj się</strong>
                        </div>
                        <div className="flex justify-center">
                            <Button type="submit" label="Zaloguj się"
                                    className="bg-gray-800 w-full mt-6 h-10 rounded-md font-medium text-white"/>
                        </div>
                    </form>
                </FormProvider>
            </div>
        </>
    );
};

export default LoginPage;
