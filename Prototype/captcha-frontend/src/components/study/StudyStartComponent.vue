<template>
  <div class="StudyStartComponent">
    <p> In this study two challenges of a new type of Captcha, named "semantic NLP CAPTCHA" will be tested. Your main task will be to
      determine the degree of meaning relatedness of two words in given sentences. </p>
   <p> For further information about Captchas see here: <a href="https://en.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA - Wikipedia</a></p>
    <p> The study will take approximately 10 to 15 minutes to complete. Please do not take any breaks while solving the
      Captcha challenges.</p>
    <p> There will be <strong> no </strong> personal data collected, the participant answers and metrics ( such as
      mother tongue and taken time ) will be stored anonymously. </p>
    <p>Notice: Try not to reload the page or move backward a page or you have to restart the Study.</p>
    <p> If you are ready press the button below. </p>
    <button id="startStudyButton" @click="startStudy"> Start Study</button>
  </div>
</template>

<script>
export default {
  name: "StudyStartComponent",
  data() {
    return {
      studyStarted: false
    };
  },
  methods: {
    startStudy() {
      this.studyStarted = true;
      this.$emit("studyStarted", true);
    }
  }
};

</script>

<style scoped>

</style>