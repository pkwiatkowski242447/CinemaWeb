import {FC} from "react";
import {Outlet} from "react-router-dom";
import Nav from "../components/Nav";

const Layout: FC = () => {
    return (
        <div className="bg-white h-screen flex items-center">
            <div>
                <Nav/>
            </div>
            <div className="flex-1 bg-transparent h-[85vh] w-full">
                <Outlet/>
            </div>
        </div>
    );
};

export default Layout;
