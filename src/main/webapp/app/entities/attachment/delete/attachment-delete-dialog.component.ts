import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAttachment } from '../attachment.model';
import { AttachmentService } from '../service/attachment.service';

@Component({
  templateUrl: './attachment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AttachmentDeleteDialogComponent {
  attachment?: IAttachment;

  protected attachmentService = inject(AttachmentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.attachmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}