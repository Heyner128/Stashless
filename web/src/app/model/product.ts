import type { User } from "../../shared/model/user";

export type Product = {
    id: string;
    name: string;
    description: string;
    brand: string;
    user: User;
    createdAt: Date;
    updatedAt: Date;
}