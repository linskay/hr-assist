import { apiService } from './api';

// =====================================================================================
// NOTE: These are placeholder functions for features that are not yet implemented in the backend.
// They return mock data to allow for UI development.
// They should be replaced with real API calls once the backend endpoints are available.
// =====================================================================================


/**
 * Placeholder for Resume Analysis.
 * This function simulates an API call to analyze a resume against a vacancy.
 * @param resumeText - The text of the candidate's resume.
 * @param vacancyText - The text of the job vacancy description.
 * @returns A promise that resolves to a mock analysis result.
 */
export const analyzeResume = async (resumeText: string, vacancyText: string) => {
  console.log('Analyzing resume (MOCK)...', { resumeText, vacancyText });
  // TODO: Replace with a real API call:
  // return await apiService.post('/analysis/resume', { resumeText, vacancyText });

  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 1000));

  // Return mock data
  return {
    score: Math.floor(Math.random() * 50) + 50,
    summary: 'Это моковые данные. Кандидат хорошо подходит по ключевым навыкам. Требуется дополнительная проверка опыта.',
  };
};

/**
 * Placeholder for getting the Competency Matrix.
 * @returns A promise that resolves to a mock competency matrix.
 */
export const getCompetencyMatrix = async () => {
    console.log('Fetching competency matrix (MOCK)...');
    // TODO: Replace with a real API call:
    // return await apiService.get('/competencies');
    await new Promise(resolve => setTimeout(resolve, 500));
    return [
        { id: 1, name: 'Java', weight: 40 },
        { id: 2, name: 'Spring Boot', weight: 30 },
        { id: 3, name: 'SQL', weight: 20 },
        { id: 4, name: 'Communication Skills', weight: 10 },
    ];
}

/**
 * Placeholder for saving the Competency Matrix.
 * @param competencies - The competency matrix to save.
 * @returns A promise that resolves to a mock status.
 */
export const saveCompetencyMatrix = async (competencies: any[]) => {
    console.log('Saving competency matrix (MOCK)...', competencies);
    // TODO: Replace with a real API call:
    // return await apiService.post('/competencies', { competencies });
    await new Promise(resolve => setTimeout(resolve, 1000));
    return { status: 'success' };
}
