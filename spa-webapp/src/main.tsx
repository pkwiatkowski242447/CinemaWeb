import React from "react";
import ReactDOM from "react-dom/client";
import {createBrowserRouter, RouteObject, RouterProvider,} from "react-router-dom";
import Layout from "./pages/Layout";
import {UnprotectedRoutes, UsersRoutes} from "./routes";
import "./index.css";
import UserContextProvider from "./context/userContext";

const router = createBrowserRouter([
    {
        path: "/",
        Component: Layout,
        children: [
            ...UnprotectedRoutes,
            {
                path: "/",
            },
        ],
    },

    {
        path: "/users",
        Component: Layout,
        children: UsersRoutes,
    },

] satisfies RouteObject[]);

ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <UserContextProvider>
            <RouterProvider router={router}/>
        </UserContextProvider>
    </React.StrictMode>
);
