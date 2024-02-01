import {FC, useEffect} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import UsersTable from "../../../components/UsersTable.tsx";
import {useUserContext} from "../../../context/userContext.tsx";
import {useNavigate} from "react-router-dom";
import {AccountTypeEnum} from "../../../types/accountType.ts";

const ClientsPage: FC = () => {
    const {account} = useUserContext();
    const navigation = useNavigate();

    useEffect(() => {
        if (account?.role[0] !== AccountTypeEnum.ADMIN) {
            navigation("/");
        }
    }, [account, navigation]);
    return (
        <>
            {account?.role[0] === AccountTypeEnum.ADMIN && (
                <div>
                    <UsersTable role={"clients"}/>
                </div>
            )}
        </>
    );
};

export default ClientsPage;
