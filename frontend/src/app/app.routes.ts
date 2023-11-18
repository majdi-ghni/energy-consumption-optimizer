import {RouterModule, Routes} from '@angular/router';
import {NgModule} from "@angular/core";
import {RegisterComponent} from "./pages/register/register.component";

export const routes: Routes = [
{
path: 'register',
component: RegisterComponent
}
];

@NgModule({
imports: [RouterModule.forRoot(routes), RouterModule ],
exports: [RouterModule]
})
export class AppRoutingModule {}
