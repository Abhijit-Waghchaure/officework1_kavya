import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { Chart, registerables } from 'chart.js';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit {

  selectedMenu = 'dashboard';
  showUserModal = false;
  filterStatus = '';   // NEW
  mail:any;

  users: any[] = [];
  leads: any[] = [];
  projects: any[] = [];

  tasks: any[] = [];
selectedTaskDate: string = '';
  displayName: any;

loadAdminTasksByDate() {
  if (!this.selectedTaskDate) {
    this.tasks = [];
    return;
  }

  this.http.get<any>(
    `${this.baseUrl}/tasks/admin/by-date?date=${this.selectedTaskDate}`
  ).subscribe(res => {
    if (res.success) {
      this.tasks = res.data;
    } else {
      this.tasks = [];
    }
  });
}

resetTaskFilter() {
  this.selectedTaskDate = '';
  this.loadAllTasks();
}


loadAllTasks() {
  this.http.get<any>(
    `${this.baseUrl}/tasks/admin/all`
  ).subscribe(res => {
    if (res.success) {
      this.tasks = res.data;
    } else {
      this.tasks = [];
    }
  });
}



  dashboardSummary: any = {};
  leadFunnel: any = {};
  leadFunnelRows: { status: string; count: number }[] = [];
  salesPerformance: any[] = [];

  showAssignProjectModal = false;
  showDescModal = false;
  showEditModal = false;
  showEditProjectModal = false;

editProjectForm: any = {};


  filterMail = '';
  selectedDescription = '';
  leadForm: any = {};

  // statuses = ['Interested', 'Not Interested', 'Follow Up', 'Closed'];

  statuses = [
  'Interested',
  'Not Interested',
  'Follow Up',
  'Quotation',
  'Call',
  'Closed',
];

  startDate = '';
  endDate = '';


  filterTaskMail = '';
filterTaskDate = '';
applyTaskFilters() {
  const params: any = {};

  if (this.filterTaskMail) {
    params.mail = this.filterTaskMail;
  }

  if (this.filterTaskDate) {
    params.date = this.filterTaskDate;
  }

  this.http.get<any>(
    `${this.baseUrl}/tasks/filter`,
    { params }
  ).subscribe(res => {
    if (res.success) {
      this.tasks = res.data;
    }
  });
}

resetTaskFilters() {
  this.filterTaskMail = '';
  this.filterTaskDate = '';
  this.loadAllTasks();
}


  projectForm: any = {
    id: '',
    leadid: '',
    projectName: '',
    clientName: '',
    projectType: '',
    budget: '',
    salesMail: '',
    teamName: '',
    teamNo: '',
    startDate: '',
    endDate: '',
    status: 'Active'
  };

  newUser = {
    id: null,
    firstname: '',
    lastname: '',
    mail: '',
    password: '',
    role: 'USER'
  };

  private baseUrl = 'http://localhost:8080';

  // 🔹 Chart instances
  leadsChart: Chart<'doughnut', number[], string> | null = null;
projectsChart: Chart<'doughnut', number[], string> | null = null;


  constructor(
    private router: Router,
    private http: HttpClient
  ) {
    // ✅ REQUIRED for Chart.js
    Chart.register(...registerables);
  }

  ngOnInit() {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const role = localStorage.getItem('role');
    this.mail=localStorage.getItem('mail');
    if (isLoggedIn !== 'true' || role !== 'ADMIN') {
      this.router.navigate(['/login']);
      return;
    }

    if (this.mail) {
    this.displayName = this.mail.split('@')[0];
  }

    this.loadDashboard();
    this.getLeadFunnel();
    this.getSalesPerformance();
    this.getAllUsers();
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  selectMenu(menu: string) {
    this.selectedMenu = menu;

    if (menu === 'dashboard') {
      this.loadDashboard();
      this.getLeadFunnel();
    }
    if (menu === 'users') this.getAllUsers();
    if (menu === 'leads') {
      this.filterMail = '';
      this.getAllLeads();
    }
    if (menu === 'projects') this.getAllProjects();
    if (menu === 'tasks') {
  // this.selectedTaskDate = new Date().toISOString().split('T')[0];
  // this.loadAdminTasksByDate();

  this.loadAllTasks();

}

  }

  /* ================= USERS ================= */

  getAllUsers() {
    this.http.get<any>(`${this.baseUrl}/getalluser`)
      .subscribe(res => {
        if (res.success) this.users = res.data;
      });
  }

  addUser() {
    this.http.post<any>(`${this.baseUrl}/registeruser`, this.newUser)
      .subscribe(res => {
        if (res.success) {
          this.resetForm();
          this.getAllUsers();
        }
      });
  }

  editUser(user: any) {
    this.newUser = { ...user };
  }

  updateUser() {
    this.http.put<any>(`${this.baseUrl}/updateuser`, this.newUser)
      .subscribe(res => {
        if (res.success) {
          this.resetForm();
          this.getAllUsers();
        }
      });
  }

  deleteUser(id: number) {
    if (!confirm('Are you sure?')) return;

    this.http.delete<any>(`${this.baseUrl}/removeuser/${id}`)
      .subscribe(res => {
        if (res.success) this.getAllUsers();
      });
  }

  resetForm() {
    this.newUser = {
      id: null,
      firstname: '',
      lastname: '',
      mail: '',
      password: '',
      role: 'USER'
    };
  }

  /* ================= LEADS ================= */

  getAllLeads() {
    this.http.get<any>(`${this.baseUrl}/getallleads`)
      .subscribe(res => {
        if (res.success) {
          this.leads = res.data.sort((a: any, b: any) => b.id - a.id);
        }
      });
  }

  getLeadsByMail() {
    if (!this.filterMail) {
      this.getAllLeads();
      return;
    }

    this.http.get<any>(`${this.baseUrl}/getleads/${this.filterMail}`)
      .subscribe(res => {
        if (res.success) {
          this.leads = res.data.sort((a: any, b: any) => b.id - a.id);
        }
      });
  }

  getTodayLeads() {
    this.http.get<any>(`${this.baseUrl}/leads/today`)
      .subscribe(res => {
        if (res.success) this.leads = res.data;
      });
  }

  getLeadsByDateRange() {
    if (!this.startDate || !this.endDate) return;

    this.http.get<any>(
      `${this.baseUrl}/leads/by-date?start=${this.startDate}&end=${this.endDate}`
    ).subscribe(res => {
      if (res.success) this.leads = res.data;
    });
  }

  openEditLead(lead: any) {
    this.leadForm = { ...lead };
    this.showEditModal = true;
  }

  saveLead() {
    this.http.put<any>(`${this.baseUrl}/updatelead`, this.leadForm)
      .subscribe(res => {
        if (res.success) {
          this.showEditModal = false;
          this.getAllLeads();
        }
      });
  }

  openDescription(desc: string) {
    this.selectedDescription = desc || 'No description provided.';
    this.showDescModal = true;
  }

  closeDescription() {
    this.showDescModal = false;
    this.selectedDescription = '';
  }

  closeEditModal() {
    this.showEditModal = false;
  }

  /* ================= PROJECTS ================= */

  getAllProjects() {
    this.http.get<any>(`${this.baseUrl}/getallprojects`)
      .subscribe(res => {
        if (res.success) this.projects = res.data;
      });
  }

  openAssignProject(lead: any) {
    this.projectForm = {
      leadid: lead.id,
      projectName: lead.projectType,
      clientName: lead.clientName,
      projectType: lead.projectType,
      budget: lead.budgetRange,
      salesMail: lead.mail,
      teamName: '',
      teamNo: '',
      startDate: '',
      endDate: '',
      status: 'Active'
    };
    this.showAssignProjectModal = true;
  }

  saveProject() {
    this.http.post<any>(`${this.baseUrl}/assignproject`, this.projectForm)
      .subscribe(res => {
        if (res.success) {
          this.getAllLeads();
          this.getAllProjects();
          this.showAssignProjectModal = false;
        }
      });
  }

  closeAssignProject() {
    this.showAssignProjectModal = false;
  }

  /* ================= DASHBOARD ================= */

  loadDashboard() {
    this.http.get<any>(`${this.baseUrl}/admindashboard/summary`)
      .subscribe(res => {
        if (res.success) {
          this.dashboardSummary = res.data;
          setTimeout(() => this.renderDashboardCharts(), 0);
        }
      });
  }

  getLeadFunnel() {
    this.http.get<any>(`${this.baseUrl}/admindashboard/lead-summary`)
      .subscribe(res => {
        if (res.success) {
          this.leadFunnel = res.data;
          this.leadFunnelRows = Object.keys(this.leadFunnel).map(key => ({
            status: key,
            count: this.leadFunnel[key]
          }));
        }
      });
  }

  getSalesPerformance() {
    this.http.get<any>(`${this.baseUrl}/admindashboard/sales-performance`)
      .subscribe(res => {
        if (res.success) this.salesPerformance = res.data;
      });
  }

  getSalesPerformanceByDate() {
  if (!this.startDate || !this.endDate) return;

  this.http.get<any>(
    `${this.baseUrl}/admindashboard/sales-performance/by-date?start=${this.startDate}&end=${this.endDate}`
  ).subscribe(res => {
    if (res.success) {
      this.salesPerformance = res.data;
    }
  });
}


  /* ================= CHARTS ================= */

  renderDashboardCharts() {
    // ---- LEADS DONUT ----
    const leadsCanvas = document.getElementById('leadsDonut') as HTMLCanvasElement;
    if (this.leadsChart) this.leadsChart.destroy();

    this.leadsChart = new Chart(leadsCanvas, {
      type: 'doughnut',
      data: {
        labels: ['Assigned', 'Unassigned'],
        datasets: [{
          data: [
            this.dashboardSummary.assignedLeads,
            this.dashboardSummary.totalLeads - this.dashboardSummary.assignedLeads
          ],
          backgroundColor: ['#2563eb', '#cbd5e1']
        }]
      },
      options: {
        plugins: { legend: { position: 'bottom' } },
        responsive: true
      }
    });

    // ---- PROJECTS DONUT ----
    const projectsCanvas = document.getElementById('projectsDonut') as HTMLCanvasElement;
    if (this.projectsChart) this.projectsChart.destroy();

    this.projectsChart = new Chart(projectsCanvas, {
      type: 'doughnut',
      data: {
        labels: ['Active', 'Inactive'],
        datasets: [{
          data: [
            this.dashboardSummary.activeProjects,
            this.dashboardSummary.totalProjects - this.dashboardSummary.activeProjects
          ],
          backgroundColor: ['#16a34a', '#e5e7eb']
        }]
      },
      options: {
        plugins: { legend: { position: 'bottom' } },
        responsive: true
      }
    });
  }

  openEditProject(project: any) {
  this.editProjectForm = { ...project };
  this.showEditProjectModal = true;
}
updateProject() {
  this.http.put<any>(
    `${this.baseUrl}/updateproject`,
    this.editProjectForm
  ).subscribe(res => {
    if (res.success) {
      this.showEditProjectModal = false;
      this.getAllProjects();
    }
  });
}
closeEditProjectModal() {
  this.showEditProjectModal = false;
}

applyLeadFilters() {
  const params: any = {};

  if (this.filterMail) {
    params.mail = this.filterMail;
  }

  if (this.filterStatus) {
    params.status = this.filterStatus;
  }

  if (this.startDate) {
    params.start = this.startDate;
  }

  if (this.endDate) {
    params.end = this.endDate;
  }

  this.http.get<any>(
    `${this.baseUrl}/leads/filter`,
    { params }
  ).subscribe(res => {
    if (res.success) {
      this.leads = res.data;
    }
  });
}


resetLeadFilters() {
  this.filterMail = '';
  this.filterStatus = '';
  this.startDate = '';
  this.endDate = '';
  this.getAllLeads();
}

isSidebarOpen = false;

toggleSidebar() {
  this.isSidebarOpen = !this.isSidebarOpen;
}

closeSidebar() {
  this.isSidebarOpen = false;
}




}
