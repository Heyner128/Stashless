import { Routes } from '@angular/router';   
import { LoginComponent } from './component/login/login.component';
import { LayoutComponent } from './component/layout/layout.component';
import { loginGuard } from './guard/login.guard';
import { InventoriesComponent } from './component/inventories/inventories.component';
import { CreateInventoryComponent  } from './component/inventories/create-inventory/create-inventory.component';
import { EditInventoryComponent  } from './component/inventories/edit-inventory/edit-inventory.component';
import { HomeComponent } from './component/home/home.component';
import { SignupComponent } from './component/signup/signup.component';
import { ErrorComponent } from './component/error/error.component';
import { ProductsComponent } from './component/products/products.component';
import { CreateProductComponent  } from './component/products/create-product/create-product.component';
import { EditProductComponent  } from './component/products/edit-product/edit-product.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'signup',
    component: SignupComponent,
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [loginGuard],
    children: [
      {
        path: '',
        component: HomeComponent,
      },
      {
        path: 'inventories',
        component: InventoriesComponent,
      },
      {
        path: 'inventories/create',
        component: CreateInventoryComponent,
      },
      {
        path: 'inventories/:id',
        component: EditInventoryComponent,
      },
      {
        path: 'products',
        component: ProductsComponent,
      },
      {
        path: 'products/create',
        component: CreateProductComponent
      },
      {
        path: 'products/:id',
        component: EditProductComponent
      },
      {
        path: 'error',
        component: ErrorComponent,
      }
    ],
  }
];
