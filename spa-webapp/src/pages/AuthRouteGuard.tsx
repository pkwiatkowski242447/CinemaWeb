import {FC} from "react";
import {Outlet} from "react-router-dom";

const AuthRouteGuard: FC = () => {
    return <Outlet/>;
};

export default AuthRouteGuard;
