import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ContractListComponent } from './contracts/contract-list.component';
import { ContractCreateComponent } from './contracts/contract-create.component';
import { TransactionListComponent } from './transactions/transaction-list.component';
import { TransactionCreateComponent } from './transactions/transaction-create.component';
import { BudgetListComponent } from './budgets/budget-list.component';
import { BudgetCreateComponent } from './budgets/budget-create.component';

export const routes: Routes = [
    {
        path: '',
        component: LayoutComponent,
        children: [
            { path: '', component: DashboardComponent },
            { path: 'contracts', component: ContractListComponent },
            { path: 'contracts/new', component: ContractCreateComponent },
            { path: 'transactions', component: TransactionListComponent },
            { path: 'transactions/new', component: TransactionCreateComponent },
            { path: 'budgets', component: BudgetListComponent },
            { path: 'budgets/new', component: BudgetCreateComponent },
        ]
    }
];
