import React, {FC, useState} from "react";
import {FormProvider, useForm} from "react-hook-form";
import Input from "../../components/fields/Input";
import Button from "../../components/Button";
import {RegisterRequest} from "../../types/registerRequest";
import {api} from "../../api/api.config.ts";
import {useNavigate} from "react-router-dom";
import logo2 from "../assets/logo2.gif";

const RegisterPage: FC = () => {
    const methods = useForm<RegisterRequest>({
        values: {
            userLogin: "",
            userPassword: "",
        },
    });

    const {handleSubmit} = methods;
    const navigation = useNavigate();

    const [selectedUserType, setSelectedUserType] = useState<string>("client");

    const onSubmit = handleSubmit((values) => {
        api.post(`/auth/register/${selectedUserType.toLowerCase()}`, values)
            .then(response => {
                navigation("/login");
                console.log(response)
                if (response != null || response != undefined) {
                    alert("Utworzono użytkownika. Proszę się zalogować.")
                }

            })
            .catch(error => {
                console.error(error);
            });
    });

    return (
        <>
            <br/><br/><br/>
            <div className="flex justify-center">
                <img src={logo2} alt="Loading GIF"/>
            </div>
            <p className="font-sans text-2xl font-bold mt-4 text-center text-gray-800">Rejestracja</p>
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
                        <div className="flex justify-center">
                            <Button type="submit" label="Zarejestruj się"
                                    className="bg-gray-800 w-full mt-6 h-10 rounded-md font-medium text-white"/>
                        </div>
                    </form>
                </FormProvider>
            </div>
        </>
    );
}

export default RegisterPage;
