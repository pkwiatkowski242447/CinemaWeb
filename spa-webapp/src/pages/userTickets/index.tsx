import {FC, useEffect} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import UserTicketsTable from "../../components/UserTicketsTable.tsx";
import {useUserContext} from "../../context/userContext.tsx";
import {useNavigate} from "react-router-dom";
import {AccountTypeEnum} from "../../types/accountType.ts";

const UserTicketsPage: FC = () => {
    const {account} = useUserContext();
    const navigation = useNavigate();

    useEffect(() => {
        if (account?.role[0] !== AccountTypeEnum.CLIENT) {
            navigation("/");
        }
    }, [account, navigation]);
    return (
        <>
            {account?.role[0] === AccountTypeEnum.CLIENT && (
                <div>
                    <br/>
                    <br/>
                    <UserTicketsTable/>
                </div>
            )}
        </>
    );
};

export default UserTicketsPage;
