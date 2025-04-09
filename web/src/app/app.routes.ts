import { Routes } from '@angular/router';   
import { LoginComponent } from './component/login/login.component';
import { LayoutComponent } from './component/layout/layout.component';
import { loginGuard } from './guard/login.guard';
import { InventoriesComponent } from './component/inventories/inventories.component';
import { DetailsComponent as InventoriesDetailsComponent } from './component/inventories/details/details.component';
import { HomeComponent } from './component/home/home.component';
import { SignupComponent } from './component/signup/signup.component';

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
        path: 'inventories/:id',
        component: InventoriesDetailsComponent,
      },
    ],
  },
];
