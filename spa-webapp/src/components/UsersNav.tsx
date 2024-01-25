import {FC, ReactNode} from "react";

type props = {
    children: ReactNode,
}

const UsersNav: FC<props> = ({children}) => {
    return (
        <nav className="flex w-screen justify-around mt-8 h-16">
            {children}
        </nav>
    );
}

export default UsersNav;