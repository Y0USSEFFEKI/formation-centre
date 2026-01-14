import { ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { StudentService } from '../../core/student.service';
import { FormateurService } from '../../core/formateur.service';
import { Cours, EtudiantProfile, Note, Seance, Groupe } from '../../core/models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../core/auth.service';

interface TimeSlot {
  label: string;
  start: string;
  end: string;
}

interface ScheduleCell {
  slot: TimeSlot;
  sessions: Seance[];
}

interface DaySchedule {
  day: string;
  slots: ScheduleCell[];
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  profileForm: FormGroup;

  profile?: EtudiantProfile;
  myCourses: Cours[] = [];
  myGroupes: Groupe[] = [];
  schedule: DaySchedule[] = [];
  notesIndex: Record<number, Record<string, number>> = {};
  activeTab: 'profile' | 'courses' | 'groupes' | 'schedule' = 'profile';
  isFormateur = false;
  timeSlots: TimeSlot[] = [
    { label: '08h30 - 11h30', start: '08:30', end: '11:30' },
    { label: '11h30 - 14h30', start: '11:30', end: '14:30' },
    { label: '14h30 - 17h30', start: '14:30', end: '17:30' }
  ];

  saving = false;
  savedMessage = '';

  // Profile UI state
  loadingProfile = false;
  profileMessage = '';

  private destroyRef = inject(DestroyRef);

  constructor(
    private fb: FormBuilder,
    private student: StudentService,
    private formateur: FormateurService,
    private auth: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.profileForm = this.fb.group({
      prenom: ['', [Validators.required]],
      nom: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit() {
    // Détecter le rôle
    this.isFormateur = this.auth.hasRole('ROLE_FORMATEUR');

    if (this.isFormateur) {
      // Charger les données pour le formateur
      this.activeTab = 'profile';
      this.loadGroupes();
      this.loadScheduleFormateur();

      // UI state for profile
      this.loadingProfile = true;
      this.profileMessage = '';

      // Récupérer le profil formateur et pré-remplir le formulaire
      this.formateur
        .getProfile()
        .pipe(
          takeUntilDestroyed(this.destroyRef),
          catchError((err) => {
            console.error('Failed to load formateur profile', err);
            this.profileMessage = "Impossible de charger le profil du formateur.";
            return of(null as any);
          }),
          finalize(() => {
            this.loadingProfile = false;
            this.cdr.detectChanges();
          })
        )
        .subscribe((profile) => {
          if (!profile) {
            // leave form as-is and display message
            return;
          }

          // map FormateurProfile -> EtudiantProfile shape used by the form
          this.profile = {
            id: profile.id,
            prenom: profile.prenom,
            nom: profile.nom,
            email: profile.email
          };
          this.profileForm.patchValue({
            prenom: profile.prenom,
            nom: profile.nom,
            email: profile.email
          });
          this.cdr.detectChanges();
        });
    } else {
      // Charger les données pour l'étudiant
      this.activeTab = 'profile';
      this.student
        .loadProfile()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((profile) => {
          this.profile = profile;
          this.profileForm.patchValue({
            prenom: profile.prenom,
            nom: profile.nom,
            email: profile.email
          });
          this.cdr.detectChanges();
        });

      this.student.myCourses$
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((courses) => {
          this.myCourses = courses;
          this.cdr.detectChanges();
        });

      this.student
        .loadMyCourses()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe();

      this.loadNotes();
      this.loadSchedule();
    }
  }

  saveProfile() {
    if (this.profileForm.invalid) {
      return;
    }

    this.saving = true;
    this.savedMessage = '';

    const prenom = this.profileForm.value.prenom || '';
    const nom = this.profileForm.value.nom || '';
    const email = this.profileForm.value.email || '';

    if (this.isFormateur) {
      const payload = { prenom, nom, email };
      this.formateur.updateProfile(payload).subscribe({
        next: (profile) => {
          this.profile = {
            id: profile.id,
            prenom: profile.prenom,
            nom: profile.nom,
            email: profile.email
          };
          this.savedMessage = 'Profil mis a jour.';
          this.cdr.detectChanges();
        },
        complete: () => {
          this.saving = false;
          this.cdr.detectChanges();
          setTimeout(() => (this.savedMessage = ''), 2000);
        }
      });
    } else {
      const payload: EtudiantProfile = {
        ...this.profile!,
        prenom,
        nom,
        email
      };

      this.student.updateProfile(payload).subscribe({
        next: (profile) => {
          this.profile = profile;
          this.savedMessage = 'Profil mis a jour.';
          this.cdr.detectChanges();
        },
        complete: () => {
          this.saving = false;
          this.cdr.detectChanges();
          setTimeout(() => (this.savedMessage = ''), 2000);
        }
      });
    }
  }

  cancelEnrollment(coursId: number) {
    this.student.cancelEnrollment(coursId).subscribe({
      next: () => {
        this.cdr.detectChanges();
        this.loadNotes();
        this.loadSchedule();
      }
    });
  }

  // UI state for groupes
  groupesMessage = '';
  loadingGroupes = false;

  private loadGroupes() {
    this.loadingGroupes = true;
    this.groupesMessage = '';

    this.formateur
      .getGroupes()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError((err) => {
          console.error('Failed to load groupes', err);
          this.groupesMessage = 'Impossible de charger les groupes pour le moment.';
          return of([]);
        }),
        finalize(() => {
          this.loadingGroupes = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe((groupes) => {
        console.log('Groupes reçus du backend:', groupes);
        this.myGroupes = groupes || [];
        if (!this.myGroupes || this.myGroupes.length === 0) {
          this.groupesMessage = 'Aucun groupe assigné pour le moment.';
        } else {
          this.groupesMessage = '';
        }
        this.cdr.detectChanges();
      });
  }

  private loadScheduleFormateur() {
    this.formateur
      .getSchedule()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((seances) => {
        this.schedule = this.buildScheduleFormateur(seances);
        this.cdr.detectChanges();
      });
  }

  private buildScheduleFormateur(seances: Seance[]): DaySchedule[] {
    const days = [
      { label: 'Lun', index: 1 },
      { label: 'Mar', index: 2 },
      { label: 'Mer', index: 3 },
      { label: 'Jeu', index: 4 },
      { label: 'Ven', index: 5 },
      { label: 'Sam', index: 6 }
    ];
    const slotRanges = this.timeSlots.map((slot) => ({
      slot,
      start: this.timeToMinutes(slot.start),
      end: this.timeToMinutes(slot.end)
    }));

    const validSeances = seances
      .filter((s) => s.heureDebut && s.heureFin)
      .sort((a, b) => a.heureDebut.localeCompare(b.heureDebut));

    return days.map((day) => {
      const slots = slotRanges.map(({ slot, start, end }) => {
        const sessions = validSeances.filter((s) => {
          const startDate = new Date(s.heureDebut);
          const endDate = new Date(s.heureFin);
          if (Number.isNaN(startDate.getTime()) || Number.isNaN(endDate.getTime())) {
            return false;
          }
          if (startDate.getDay() !== day.index) {
            return false;
          }
          const sStart = startDate.getHours() * 60 + startDate.getMinutes();
          const sEnd = endDate.getHours() * 60 + endDate.getMinutes();
          return sStart < end && sEnd > start;
        });
        return { slot, sessions };
      });
      return { day: day.label, slots };
    });
  }

  formatTime(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }
    return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }

  private loadSchedule() {
    this.student
      .getSchedule()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((seances) => {
        this.schedule = this.buildSchedule(seances);
        this.cdr.detectChanges();
      });
  }

  private loadNotes() {
    this.student
      .getNotes()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((notes) => {
        console.log('Notes reçues du backend:', notes);
        this.notesIndex = this.buildNotesIndex(notes);
        console.log('Index des notes:', this.notesIndex);
        this.cdr.detectChanges();
      });
  }

  private buildNotesIndex(notes: Note[]): Record<number, Record<string, number>> {
    const index: Record<number, Record<string, number>> = {};

    notes.forEach((note) => {
      if (!note.coursId) {
        return;
      }
      const type = (note.type || '').trim().toLowerCase();
      const bucket = type === 'exam' ? 'exam' : 'ds';
      if (!index[note.coursId]) {
        index[note.coursId] = {};
      }
      if (note.valeur !== undefined && note.valeur !== null) {
        index[note.coursId][bucket] = note.valeur;
      }
    });

    return index;
  }

  noteValue(coursId: number, type: string): string {
    const key = type.toLowerCase();
    const value = this.notesIndex[coursId]?.[key];
    if (value === undefined || value === null) {
      return '_';
    }
    return value.toString();
  }

  private buildSchedule(seances: Seance[]): DaySchedule[] {
    const days = [
      { label: 'Lun', index: 1 },
      { label: 'Mar', index: 2 },
      { label: 'Mer', index: 3 },
      { label: 'Jeu', index: 4 },
      { label: 'Ven', index: 5 },
      { label: 'Sam', index: 6 }
    ];
    const slotRanges = this.timeSlots.map((slot) => ({
      slot,
      start: this.timeToMinutes(slot.start),
      end: this.timeToMinutes(slot.end)
    }));

    const validSeances = seances
      .filter((s) => s.heureDebut && s.heureFin)
      .sort((a, b) => a.heureDebut.localeCompare(b.heureDebut));

    return days.map((day) => {
      const slots = slotRanges.map(({ slot, start, end }) => {
        const sessions = validSeances.filter((s) => {
          const startDate = new Date(s.heureDebut);
          const endDate = new Date(s.heureFin);
          if (Number.isNaN(startDate.getTime()) || Number.isNaN(endDate.getTime())) {
            return false;
          }
          if (startDate.getDay() !== day.index) {
            return false;
          }
          const sStart = startDate.getHours() * 60 + startDate.getMinutes();
          const sEnd = endDate.getHours() * 60 + endDate.getMinutes();
          return sStart < end && sEnd > start;
        });
        return { slot, sessions };
      });
      return { day: day.label, slots };
    });
  }

  private timeToMinutes(value: string): number {
    const [hours, minutes] = value.split(':').map((part) => Number(part));
    return (Number.isNaN(hours) ? 0 : hours) * 60 + (Number.isNaN(minutes) ? 0 : minutes);
  }

  selectTab(tab: 'profile' | 'courses' | 'groupes' | 'schedule') {
    this.activeTab = tab;
  }
}
