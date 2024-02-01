import {FC} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import UsersTable from "../../../components/UsersTable.tsx";

const ClientsPage: FC = () => {

    return (
        <div>
            <UsersTable role={"clients"}/>
        </div>
    );
};

export default ClientsPage;
