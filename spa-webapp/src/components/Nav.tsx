import {FC} from "react";
import {NavLink} from "react-router-dom";
import {IconType} from "react-icons";
import {classNames} from "../utils/tailwind";
import {CiBank, CiFloppyDisk, CiUser} from "react-icons/ci";

type MenuItem = {
    to: string;
    icon: IconType;
    label: string;
};

const Nav: FC = () => {
    const menuItems: MenuItem[] = [
        {to: "../users", icon: CiUser, label: "Zarządzaj Użytkownikami"},
        {to: "../movies", icon: CiFloppyDisk, label: "Zarządzaj Filmami"},
        {to: "../tickets", icon: CiBank, label: "Zarządzaj Biletami"},
    ];

    return (
        <div className="shadow-lg fixed top-0 w-full bg-gray-700">
            <ul className="w-full flex justify-evenly items-center">
                {menuItems.map(({to, icon: Icon, label}) => (
                    <li key={to} className="p-0.5">
                        <NavLink
                            to={to}
                            className={({isActive}) =>
                                classNames(
                                    "hover:text-gray-400 flex items-center flex-col p-2",
                                    isActive
                                        ? "text-gray-400"
                                        : "text-white"
                                )
                            }
                        >
                            <Icon className="h-10 w-10"/>
                            {label}
                        </NavLink>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default Nav;
