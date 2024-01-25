import {RouteObject} from "react-router-dom";
import MainPage from "./pages/main";
import UsersLayout from "./pages/users/UsersLayout.tsx";
import MoviesPage from "./pages/movies";
import AllTicketsPage from "./pages/tickets";

export const UnprotectedRoutes = [
    {path: "/", Component: MainPage},
    {path: "/movies", Component: MoviesPage},
    {path: "/tickets", Component: AllTicketsPage},
] satisfies RouteObject[];

export const UsersRoutes = [
    {path: "/users", Component: UsersLayout},
] satisfies RouteObject[];
