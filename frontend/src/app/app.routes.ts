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
import { NotificationsComponent } from './notifications/notifications.component';
import { GoalListComponent } from './goals/goal-list.component';
import { GoalInterviewComponent } from './goals/goal-interview.component';
import { GoalDetailComponent } from './goals/goal-detail.component';
import { LoginComponent } from './auth/login.component';
import { AccountSettingsComponent } from './account/account-settings.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: DashboardComponent, data: { titleKey: 'routes.home' } },
      { path: 'assistant', component: AssistantComponent, data: { titleKey: 'routes.assistant' } },
      {
        path: 'notifications',
        component: NotificationsComponent,
        data: { titleKey: 'routes.notifications' },
      },
      {
        path: 'account',
        component: AccountSettingsComponent,
        data: { titleKey: 'routes.account' },
      },
      { path: 'accounts', component: AccountListComponent, data: { titleKey: 'routes.accounts' } },
      {
        path: 'accounts/new',
        component: AccountCreateComponent,
        data: { titleKey: 'routes.newAccount' },
      },
      {
        path: 'accounts/:id',
        component: AccountDetailComponent,
        data: { titleKey: 'routes.account' },
      },
      {
        path: 'transactions',
        component: TransactionListComponent,
        data: { titleKey: 'routes.transactions' },
      },
      {
        path: 'transactions/new',
        component: TransactionCreateComponent,
        data: { titleKey: 'routes.newTransaction' },
      },
      {
        path: 'transactions/import',
        component: ImportComponent,
        data: { titleKey: 'routes.importCsv' },
      },
      {
        path: 'contracts',
        component: ContractListComponent,
        data: { titleKey: 'routes.contracts' },
      },
      {
        path: 'contracts/new',
        component: ContractCreateComponent,
        data: { titleKey: 'routes.newContract' },
      },
      { path: 'budgets', component: BudgetListComponent, data: { titleKey: 'routes.budgets' } },
      {
        path: 'budgets/new',
        component: BudgetCreateComponent,
        data: { titleKey: 'routes.newBudget' },
      },
      { path: 'goals', component: GoalListComponent, data: { titleKey: 'routes.goals' } },
      { path: 'goals/new', component: GoalInterviewComponent, data: { titleKey: 'routes.newGoal' } },
      { path: 'goals/:id', component: GoalDetailComponent, data: { titleKey: 'routes.goalDetail' } },
      { path: 'bank-sync', component: BankSyncComponent, data: { titleKey: 'routes.bankSync' } },
      {
        path: 'bank-sync/callback',
        component: BankSyncCallbackComponent,
        data: { titleKey: 'routes.bankSyncCallback' },
      },
      {
        path: 'categories',
        component: CategoryListComponent,
        data: { titleKey: 'routes.categories' },
      },
      {
        path: 'categories/new',
        component: CategoryCreateComponent,
        data: { titleKey: 'routes.newCategory' },
      },
    ],
  },
];
