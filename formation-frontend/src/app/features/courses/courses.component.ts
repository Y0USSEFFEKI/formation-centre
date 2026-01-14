import { ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import { finalize, forkJoin } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { CatalogueService } from '../../core/catalogue.service';
import { StudentService } from '../../core/student.service';
import { Cours, Specialite } from '../../core/models';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit {
  specialites: Specialite[] = [];
  cours: Cours[] = [];
  selectedSpecialiteId: number | null = null;
  myCourseIds = new Set<number>();

  loading = true;
  error = '';
  private destroyRef = inject(DestroyRef);

  constructor(
    private catalogue: CatalogueService,
    private student: StudentService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    forkJoin({
      specialites: this.catalogue.getSpecialites(),
      cours: this.catalogue.getCours()
    })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: ({ specialites, cours }) => {
          this.specialites = specialites;
          this.cours = cours;
          this.selectSpecialite(null);
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'Impossible de charger les cours.';
          this.cdr.detectChanges();
        }
      });

    this.student.myCourses$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((myCourses) => {
        this.myCourseIds = new Set(myCourses.map((c) => c.id));
        this.cdr.detectChanges();
      });

    this.student
      .loadMyCourses()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        error: () => {
          this.myCourseIds = new Set<number>();
          this.cdr.detectChanges();
        }
      });
  }

  selectSpecialite(id: number | null) {
    this.selectedSpecialiteId = id;
  }

  visibleSpecialites(): Specialite[] {
    if (!this.selectedSpecialiteId) {
      return this.specialites;
    }
    return this.specialites.filter((s) => s.id === this.selectedSpecialiteId);
  }

  coursBySpecialite(specialiteId: number): Cours[] {
    return this.cours.filter((c) => c.specialiteId === specialiteId);
  }

  isEnrolled(coursId: number): boolean {
    return this.myCourseIds.has(coursId);
  }

  toggleEnrollment(coursId: number) {
    if (this.isEnrolled(coursId)) {
      this.student.cancelEnrollment(coursId).subscribe({
        next: () => this.cdr.detectChanges()
      });
      return;
    }

    this.student.enroll(coursId).subscribe({
      next: () => this.cdr.detectChanges()
    });
  }
}
