import type { NewSKU, SKU } from './sku';

export type Item = {
  sku: SKU;
  quantity: number;
}


export type NewItem = Omit<
  NewSKU,
  "id" | "createdAt" | "updatedAt"
> & { quantity: number }; 