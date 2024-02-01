import React, {FC, useState} from "react";
import SubPanel from "../../components/SubPanel";
import AdminsPage from "./admins";
import StaffsPage from "./staffs";
import ClientsPage from "./clients";
import UsersNav from "../../components/UsersNav.tsx";

const panels = {
    admins: <AdminsPage/>,
    staffs: <StaffsPage/>,
    clients: <ClientsPage/>,
} as const;

export type Panel = keyof typeof panels;

const StaffLayout: FC = () => {
    const [panel, setPanel] = useState<Panel>("admins");

    const handleSubPanelClick = (
        _event: React.MouseEvent<HTMLDivElement>,
        message: Panel
    ) => {
        setPanel(message);
    };

    return (
        <>
            <UsersNav>
                <SubPanel onClick={handleSubPanelClick} message="admins">
                    <p>Administratorzy</p>
                </SubPanel>
                <SubPanel onClick={handleSubPanelClick} message="staffs">
                    <p>Pracownicy</p>
                </SubPanel>
                <SubPanel onClick={handleSubPanelClick} message="clients">
                    <p>Klienci</p>
                </SubPanel>
            </UsersNav>
            <br/>
            <main>{panels[panel]}</main>
        </>
    );
};

export default StaffLayout;
