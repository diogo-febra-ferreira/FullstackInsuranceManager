<template>
  <div class="d-flex align-items-center justify-content-center">
    <b-container>
    <h1 class="text-center mb-4" style="margin-top: 2rem"> Register as client</h1>
    <b-form @submit.prevent="create" :disabled="!isFormValid" class="mx-auto">
      <b-form-group

        description="The username is required"
        label="Enter your username"
        label-for="username"
        :invalid-feedback="invalidUsernameFeedback"
        :state="isUsernameValid"
      >
        <b-input id="username" v-model.trim="username" :state="isUsernameValid" trim></b-input>
      </b-form-group>
      <b-form-group

        description="The password is required"
        label="Enter your password"
        label-for="password"
      >
      <b-input v-model="password" type="password" :state="isPasswordValid" required
               />
      </b-form-group>
      <b-form-group

                    description="The name is required"
                    label="Enter your name"
                    label-for="name"
      >
      <b-input v-model.trim="name" :state="isNameValid" required
               />
      </b-form-group>
      <b-form-group
                    description="The email is required"
                    label="Enter your email"
                    label-for="email"
      >
      <b-input ref="email" v-model.trim="email" type="email"
               :state="isEmailValid"/>
      </b-form-group>
      <p class="text-danger" v-show="errorMsg">{{ errorMsg }}</p>
      <b-button variant="info" to="/">Return</b-button>
      <b-button variant="warning" type="reset" @click="reset">RESET</b-button>
      <b-button variant="success" @click.prevent="create"
              :disabled="!isFormValid">CREATE
      </b-button>
    </b-form>
    </b-container>
  </div>
</template>
<script>
export default {
  auth: false,
  data() {
    return {
      username: null,
      password: null,
      name: null,
      email: null,
      errorMsg: false
    }
  },
  computed: {
    invalidUsernameFeedback () {
      if (!this.username) {
        return null
      }
      let usernameLen = this.username.length
      if (usernameLen !== 9 || !Number.isInteger(Number.parseInt(this.username))) {
        return 'The username must be 9 digits.'
      }
      return ''
    },
    isUsernameValid () {
      if (this.invalidUsernameFeedback === null) {
        return null
      }
      return this.invalidUsernameFeedback === ''
    },
    isPasswordValid() {
      if (!this.password) {
        return null
      }
      let passwordLen = this.password.length
      if (passwordLen < 3 || passwordLen > 255) {
        return false
      }
      return true
    },
    isNameValid() {
      if (!this.name) {
        return null
      }
      let nameLen = this.name.length
      if (nameLen < 3 || nameLen > 25) {
        return false
      }
      return true
    },
    isEmailValid() {
      if (!this.email) {
        return null
      }

      /* asks the component if it’s valid. We don’t need to use a regex for
      the e-mail. The input field already does the job for us, because it is of type
      “email” and validates that the user writes an e-mail that belongs to the domain
      of IPLeiria.*/
      return this.$refs.email.checkValidity()
    },
    isFormValid() {
      if (!this.isUsernameValid) {
        return false
      }
      if (!this.isPasswordValid) {
        return false
      }
      if (!this.isNameValid) {
        return false
      }
      if (!this.isEmailValid) {
        return false
      }
      return true
    }
  },
  methods: {
    reset() {
      this.errorMsg = false
    },
    create() {
      this.$axios.$post('/api/auth/register', {
        username: this.username,
        password: this.password,
        name: this.name,
        email: this.email,
      })
        .then(() => {
          this.$router.push('/clients/' + this.username)
        })
        .catch(error => {
          this.errorMsg = error.response.data
        })
    }
  }
}
</script>
