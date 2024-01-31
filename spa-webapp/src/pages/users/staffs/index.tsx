import {FC} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import UsersTable from "../../../components/UsersTable.tsx";

const StaffsPage: FC = () => {

    return (
        <div>
            <UsersTable role={"staffs"}/>
        </div>
    );
};

export default StaffsPage;
