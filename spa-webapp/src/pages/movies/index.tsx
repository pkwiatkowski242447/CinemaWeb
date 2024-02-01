import {FC} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import MoviesTable from "../../components/MoviesTable.tsx";

const MoviesPage: FC = () => {

    return (
        <div>
            <br/>
            <br/>
            <MoviesTable/>
        </div>
    );
};

export default MoviesPage;
