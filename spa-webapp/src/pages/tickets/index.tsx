import {FC} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import AllTicketsTable from "../../components/AllTicketsTable.tsx";

const AllTicketsPage: FC = () => {

    return (
        <div>
            <br/>
            <br/>
            <AllTicketsTable/>
        </div>
    );
};

export default AllTicketsPage;
