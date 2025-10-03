package tekin.luetfi.resume.ui.screen.cover_letter

import tekin.luetfi.resume.domain.model.JobApplicationMail
import tekin.luetfi.resume.domain.model.MatchError

sealed class CoverLetterState {
    object Loading: CoverLetterState()
    class Error(val matchError: MatchError?): CoverLetterState()
    class Success(val mail: JobApplicationMail): CoverLetterState()

}