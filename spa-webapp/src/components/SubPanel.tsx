import {FC, MouseEvent, ReactNode} from "react";
import {Panel} from "../pages/users/UsersLayout.tsx";


type props = {
    children: ReactNode,
    message: Panel,
    onClick: (event: MouseEvent<HTMLDivElement>, message: Panel) => void
};


const SubPanel: FC<props> = ({children, message, onClick}) => {
    return (
        <div onClick={(event) => onClick(event, message)}
             className="shadow-lg  bg-gray-700 text-white border-gray-700 border-3 w-48 h-3/4 font-bold hover:cursor-pointer flex items-center justify-center rounded-md"
             style={{marginTop: '-30x'}}>
            {children}
        </div>
    );
}

export default SubPanel;