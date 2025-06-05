import type { User } from "./user";

export type Product = {
    id: string;
    name: string;
    description: string;
    brand: string;
    user: User;
    createdAt: Date;
    updatedAt: Date;
}