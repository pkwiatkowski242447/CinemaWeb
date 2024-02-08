import {getAuthToken} from "./api.config.ts";

export const logoutUser = async () => {
    const config = {
        headers: {Authorization: `Bearer ${getAuthToken()}`}
    };

    const endpoint = '/auth/logout'

    // await api.post(endpoint, config)

    window.localStorage.removeItem("token");
    window.localStorage.removeItem("account");

}
