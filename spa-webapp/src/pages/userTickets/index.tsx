import {FC} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import UserTicketsTable from "../../components/UserTicketsTable.tsx";

const UserTicketsPage: FC = () => {

    return (
        <div>
            <br/>
            <br/>
            <UserTicketsTable/>
        </div>
    );
};

export default UserTicketsPage;
