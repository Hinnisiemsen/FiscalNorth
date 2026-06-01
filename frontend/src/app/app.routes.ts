import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ContractListComponent } from './contracts/contract-list.component';
import { ContractCreateComponent } from './contracts/contract-create.component';
import { TransactionListComponent } from './transactions/transaction-list.component';
import { TransactionCreateComponent } from './transactions/transaction-create.component';
import { ImportComponent } from './import/import.component';
import { BudgetListComponent } from './budgets/budget-list.component';
import { BudgetCreateComponent } from './budgets/budget-create.component';
import { AccountListComponent } from './accounts/account-list.component';
import { AccountCreateComponent } from './accounts/account-create.component';
import { AccountDetailComponent } from './accounts/account-detail.component';
import { CategoryListComponent } from './categories/category-list.component';
import { CategoryCreateComponent } from './categories/category-create.component';
import { BankSyncComponent } from './bank-sync/bank-sync.component';
import { BankSyncCallbackComponent } from './bank-sync/bank-sync-callback.component';
import { AssistantComponent } from './assistant/assistant.component';

export const routes: Routes = [
    {
        path: '',
        component: LayoutComponent,
        children: [
            { path: '', component: DashboardComponent, data: { title: 'Home' } },
            { path: 'assistant', component: AssistantComponent, data: { title: 'Fiscal North' } },
            { path: 'accounts', component: AccountListComponent, data: { title: 'Accounts' } },
            { path: 'accounts/new', component: AccountCreateComponent, data: { title: 'New Account' } },
            { path: 'accounts/:id', component: AccountDetailComponent, data: { title: 'Account' } },
            { path: 'transactions', component: TransactionListComponent, data: { title: 'Transactions' } },
            { path: 'transactions/new', component: TransactionCreateComponent, data: { title: 'New Transaction' } },
            { path: 'transactions/import', component: ImportComponent, data: { title: 'Import CSV' } },
            { path: 'contracts', component: ContractListComponent, data: { title: 'Contracts' } },
            { path: 'contracts/new', component: ContractCreateComponent, data: { title: 'New Contract' } },
            { path: 'budgets', component: BudgetListComponent, data: { title: 'Budgets' } },
            { path: 'budgets/new', component: BudgetCreateComponent, data: { title: 'New Budget' } },
            { path: 'bank-sync', component: BankSyncComponent, data: { title: 'Bank verbinden' } },
            { path: 'bank-sync/callback', component: BankSyncCallbackComponent, data: { title: 'Bank Sync' } },
            { path: 'categories', component: CategoryListComponent, data: { title: 'Categories' } },
            { path: 'categories/new', component: CategoryCreateComponent, data: { title: 'New Category' } },
        ],
    },
];
