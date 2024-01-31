import {RouteObject} from "react-router-dom";
import MainPage from "./pages/main";
import UsersLayout from "./pages/users/UsersLayout.tsx";
import MoviesPage from "./pages/movies";
import AllTicketsPage from "./pages/tickets";
import UserTicketsPage from "./pages/userTickets";

export const UnprotectedRoutes = [
    {path: "/", Component: MainPage},
] satisfies RouteObject[];

export const AdminsRoutes = [
    {path: "/users", Component: UsersLayout},
] satisfies RouteObject[];

export const StaffRoutes = [
    {path: "/movies", Component: MoviesPage},
    {path: "/tickets", Component: AllTicketsPage},
] satisfies RouteObject[];

export const UserRoutes = [
    {path: "/buyTickets", Component: UserTicketsPage},
] satisfies RouteObject[];
