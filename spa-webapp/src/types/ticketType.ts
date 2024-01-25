import {AccountType} from "./accountType.ts";
import {MovieType} from "./movieType.ts";

export interface TicketType {
    ticketId: string,
    movieTime: string,
    ticketFinalPrice: number,
    clientId: string,
    movieId: string
}