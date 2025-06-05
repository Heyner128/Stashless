export type SKU = {
    id: string;
    name: string;
    costPrice: number;
    amountAvailable: number;
    marginPercentage: number;
    options: Record<string, string>;
    createdAt: Date;
    updatedAt: Date;
}

export type NewSKU = Omit<SKU, "id" | "createdAt" | "updatedAt">;