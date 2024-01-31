export enum AccountTypeEnum {
    ADMIN = 'ADMIN',
    STAFF = 'STAFF',
    CLIENT = 'CLIENT'
}

export interface AccountType {
    id: string
    login: string
    statusActive: boolean
    role?: AccountTypeEnum
}
