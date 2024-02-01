import {FC} from "react";
import {Outlet} from "react-router-dom";


const MoviesLayout: FC = () => {

    return (
        <div className="bg-white w-full h-screen flex items-center">
            <div className="flex-1 h-[85vh] w-full">
                <Outlet/>
            </div>
        </div>
    );
};

export default MoviesLayout;
