import React from "react";
import ReactDOM from "react-dom/client";
import {createBrowserRouter, RouteObject, RouterProvider,} from "react-router-dom";
import Layout from "./pages/Layout";
import {UnprotectedRoutes, AdminsRoutes, StaffRoutes, UserRoutes} from "./routes";
import "./index.css";
import UserContextProvider from "./context/userContext";
import AuthenticationLayout from "./pages/AuthenticationLayout.tsx";
import RegisterPage from "./pages/register";
import LoginPage from "./pages/login";
import AuthRouteGuard from "./pages/AuthRouteGuard.tsx";
import MoviesPage from "./pages/movies";
import AllTicketsPage from "./pages/tickets";

const router = createBrowserRouter([
    {
        path: "/",
        Component: Layout,
        children: [
            ...UnprotectedRoutes,
            {
                path: "/",
                Component: AuthRouteGuard,
            },
        ],
    },
    {
        path: "/",
        Component: AuthenticationLayout,
        children: [
            {path: "/register", Component: RegisterPage},
            {path: "/login", Component: LoginPage},
        ],
    },
    {
        path: "/users",
        Component: Layout,
        children: AdminsRoutes,
    },
    {
        path: "/",
        Component: Layout,
        children: [
            {path: "/tickets", Component: AllTicketsPage},
            {path: "/movies", Component: MoviesPage},
        ],
    },

    {
        path: "/buyTickets",
        Component: Layout,
        children: UserRoutes,
    },


] satisfies RouteObject[]);

ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <UserContextProvider>
            <RouterProvider router={router}/>
        </UserContextProvider>
    </React.StrictMode>
);
