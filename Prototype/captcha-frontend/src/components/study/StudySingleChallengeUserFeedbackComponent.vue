<template>
  <div class="StudyUserFeedbackComponent">
    <h1>User Feedback</h1>
    <p>This study helps to develop a new type of "Are you human?" detection
      program called "semantic NLP CAPTCHA". <br>
      Captcha in general determine if a user really is human and come in
      different forms like CheckBox Captcha or Image Captcha. </p>
    <p> For further information about Captchas see here: <a href="https://en.wikipedia.org/wiki/CAPTCHA"
                                                            target="_blank">CAPTCHA - Wikipedia</a></p>



    <p>Please provide feedback on your experience. </p>
    <h2>General Information:</h2>


    <p> Do you have / going to have an academic degree, e.g. bachelor, master, phd, etc. ? </p>
    <select v-model="academic_degree">
      <option value="yes">Yes</option>
      <option value="no">No</option>
      <option value="">No answer</option>
    </select>


    <p>Is english your mother tongue ?</p>
    <select v-model="mother_language">
      <option value="yes">Yes</option>
      <option value="no">No</option>
      <option value="">No answer</option>
    </select>

    <p>How would you rate your english language skills in general? <br> Rate on a scale from 1:
      you have no understanding at all to 10: english is your mother tongue : </p>
    <input type="number" v-model="language_skills">

    <h2>Challenge:</h2>

    <p>How easy was it to understand the sentences in the challenge in general? <br> Rate on a scale from 1
      being impossible and 10 being trivial to understand: </p>
    <input type="number" v-model="rating_determine_sentence_understanding_difficulty">

    <p>How easy was it to rate the meaning similarity of the highlighted words? <br> Rate on a scale from 1
      being impossible and 10 being trivial to rate: </p>
    <input type="number" v-model="rating_determine_similarity_difficulty">

    <p>How convenient and practical do you think is such type of "human-detection" challenge in daily life ? <br> Rate
      on a scale from 1
      being not at all convenient or practical and 10 being highly convenient and practical:</p>
    <input type="number" v-model="challenge_feedback1">

    <p>And would you prefer that challenge over any different kind of Captcha-Challenge (like Image-Captcha)? </p>
    <textarea v-model="challenge_feedback2" v-auto-grow></textarea>
    <p v-if="isTooLong_challenge" style="color: red">Your input is too long!</p>

    <p>What is your opinion on the count of items in one challenge? <br> How many would you be willing to solve without
      being majorly annoyed as a Captcha-Challenge in daily life? </p>
    <input type="number" v-model="challenge_count_opinion">

    <h2>General Feedback:</h2>
    <p>Any other feedback you would like to provide about the challenge or to improve this study?</p>
    <textarea v-model="general_feedback" v-auto-grow> </textarea>
    <p v-if="isTooLong_general" style="color: red">Your input is too long!</p>


    <p>Thank you for your participation!</p>
    <button @click="submitFeedback">Submit Feedback</button>
  </div>
</template>

<script>
export default {
  name: "StudyUserFeedbackComponent",
  data() {
    return {
      academic_degree: "",
      mother_language: "",
      language_skills: null,

      rating_determine_sentence_understanding_difficulty: null,
      rating_determine_similarity_difficulty: null,
      challenge_feedback1: "",
      challenge_feedback2: "",
      challenge_count_opinion: null,

      general_feedback: "",

      max_input_length: 3500,
      isTooLong_challenge: false,
      isTooLong_general: false
    };
  },
  methods: {
    submitFeedback() {

      // Check if all fields have been filled out
      // if (this.pair_challenge_feedback === "" ||
      //   this.pair_challenge_count_opinion === null ||
      //   this.list_challenge_feedback === "" ||
      //   this.list_challenge_count_opinion === null
      // ) {
      //   alert("Please fill out all fields before submitting.");
      //   return;
      // }

      const challFeedback = this.challenge_feedback1 + " | " + this.challenge_feedback2;

      // Create an object with all the feedback fields
      const feedback = {
        academicDegree: this.academic_degree,
        motherLanguage: this.mother_language,
        languageSkills: this.language_skills,

        ratingSentenceUnderstanding: this.rating_determine_sentence_understanding_difficulty,
        ratingDetermineSemanticMeaning: this.rating_determine_similarity_difficulty,
        ChallengeFeedback: challFeedback,
        ChallengeCountOpinion: this.challenge_count_opinion,

        generalFeedback: this.general_feedback
      };

      this.isTooLong_general = this.general_feedback.length > this.max_input_length;

      this.isTooLong_challenge = challFeedback.length > this.max_input_length;

      if (challFeedback.length <= this.max_input_length && this.general_feedback.length <= this.max_input_length) {
        this.isTooLong_general = false;
        this.isTooLong_challenge = false;
        this.$emit("submitFeedback", feedback);
      }

    }
  },
  directives: {
    autoGrow: {
      update: function(el) {
        el.style.height = "auto";
        el.style.height = (el.scrollHeight) + "px";
      }
    }
  }
};
</script>

<style scoped>
textarea {
  width: 50%;
  min-height: 25px;
  max-height: 200px;
  resize: vertical;
}

input[type="number"] {
  width: 2.5%;
}
</style>