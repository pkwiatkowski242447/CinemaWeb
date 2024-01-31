import {FC} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import UsersTable from "../../../components/UsersTable.tsx";

const AdminsPage: FC = () => {

    return (
        <div>
            <UsersTable role={"admins"}/>
        </div>
    );
};

export default AdminsPage;
