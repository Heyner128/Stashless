import type { Item } from "./item";
import type { User } from "./user";

export type Inventory = {
    id: string;
    name: string;
    description: string;
    items: Item[];
    user: User;
    createdAt: Date;
    updatedAt: Date;
}
