import {createContext, FC, ReactNode, useContext, useEffect, useState} from "react";
import {AccountType} from "../types/accountType.ts";


interface UserState {
    account: AccountType | null;
    setAccount: (item: { role: any; login: any }) => void;
}

const UserContext = createContext<UserState>({} as UserState);

interface UserContextProviderProps {
    children: ReactNode;
}

const UserContextProvider: FC<UserContextProviderProps> = ({children}: { children: ReactNode }) => {
    const [account, setAccount] = useState<AccountType | null>(() => {
        const storedAccount = localStorage.getItem("account");
        return storedAccount ? JSON.parse(storedAccount) : null;
    })

    useEffect(() => {
        localStorage.setItem("account", JSON.stringify(account));
    }, [account]);

    return <UserContext.Provider value={{account, setAccount}}>{children}</UserContext.Provider>;
};

export const useUserContext = () => {
    return useContext(UserContext);
};

export default UserContextProvider;
