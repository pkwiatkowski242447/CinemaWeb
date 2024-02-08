import {FC, useEffect} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import MoviesTable from "../../components/MoviesTable.tsx";
import {useUserContext} from "../../context/userContext.tsx";
import {useNavigate} from "react-router-dom";
import {AccountTypeEnum} from "../../types/accountType.ts";

const MoviesPage: FC = () => {
    const {account} = useUserContext();
    const navigation = useNavigate();

    useEffect(() => {
        if (account?.role[0] !== AccountTypeEnum.STAFF) {
            navigation("/");
        }
    }, [account, navigation]);

    return (
        <>
            {account?.role[0] === AccountTypeEnum.STAFF && (
                <div>
                    <br />
                    <br />
                    <MoviesTable />
                </div>
            )}
        </>
    );
};

export default MoviesPage;
