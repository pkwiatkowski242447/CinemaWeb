import {FC, ReactNode} from "react";

type props = {
    children: ReactNode,
}

const TicketsNav: FC<props> = ({children}) => {
    return (
        <nav className="flex w-screen justify-around mt-1 h-16">
            {children}
        </nav>
    );
}

export default TicketsNav;