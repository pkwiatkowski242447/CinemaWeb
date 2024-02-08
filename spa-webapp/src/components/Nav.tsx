import {FC} from "react";
import {NavLink} from "react-router-dom";
import {IconType} from "react-icons";
import {classNames} from "../utils/tailwind";
import {CiBank, CiFloppyDisk, CiMap, CiMoneyCheck1, CiUser} from "react-icons/ci";
import LogoutButton from "./LogoutButton.tsx";
import LoginButton from "./LoginButton.tsx";
import {logoutUser} from "../api/logout.ts";
import {useUserContext} from "../context/userContext.tsx";
import {AccountTypeEnum} from "../types/accountType.ts";

type MenuItem = {
    to: string;
    icon: IconType;
    label: string;
    allowedRoles: AccountTypeEnum[];
};

const Nav: FC = () => {
    const {account} = useUserContext();
    const menuItems: MenuItem[] = [
        {
            to: "../",
            icon: CiMap,
            label: "Strona Główna",
            allowedRoles: [AccountTypeEnum.STAFF, AccountTypeEnum.ADMIN, AccountTypeEnum.CLIENT]
        },
        {to: "../users", icon: CiUser, label: "Zarządzaj Użytkownikami", allowedRoles: [AccountTypeEnum.ADMIN]},
        {to: "../movies", icon: CiFloppyDisk, label: "Zarządzaj Filmami", allowedRoles: [AccountTypeEnum.STAFF]},
        {to: "../tickets", icon: CiBank, label: "Zarządzaj Biletami", allowedRoles: [AccountTypeEnum.STAFF]},
        {to: "../buyTickets", icon: CiMoneyCheck1, label: "Kup Bilet", allowedRoles: [AccountTypeEnum.CLIENT]},
    ];

    const filteredMenuItems = menuItems.filter(item => account?.role && item.allowedRoles.includes(account?.role[0]));

    return (
        <div className="shadow-lg fixed top-0 w-full bg-gray-700">
            <ul className="w-full flex justify-between items-center">
                {filteredMenuItems.map(({to, icon: Icon, label}) => (
                    <li key={to} className="p-0.5">
                        <NavLink
                            to={to}
                            className={({isActive}) =>
                                classNames(
                                    "hover:text-gray-400 flex items-center flex-col p-2",
                                    isActive ? "text-gray-400" : "text-white"
                                )
                            }
                        >
                            <Icon className="h-10 w-10"/>
                            {label}
                        </NavLink>
                    </li>
                ))}
                <li className="p-0.5 text-white">
                    {account?.role && (
                        <span>
              Zalogowano jako: <strong>{account.login}</strong><br/>Rodzaj konta: <strong>{account.role[0]}</strong>
            </span>
                    )}
                </li>
                <li>
                    {account?.role ? (
                        <LogoutButton logoutUser={logoutUser}/>
                    ) : (
                        <LoginButton/>
                    )}
                </li>
            </ul>
        </div>
    );
};

export default Nav;
