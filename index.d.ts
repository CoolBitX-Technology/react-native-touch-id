declare module 'react-native-touch-id' {
  /**
   * The supported biometry type
   */
  type BiometryType = 'FaceID' | 'TouchID' | 'Fingerprint';

  /**
   * Base config to pass to `TouchID.isSupported` and `TouchID.authenticate`
   */
  interface IsSupportedConfig {
    /**
     * Return unified error messages
     */
    unifiedErrors?: boolean;
  }

  /**
   * Authentication config
   */
  export interface AuthenticateConfig extends IsSupportedConfig {
    /**
     * **Android only** - auth reason text color
     */
    authReasonColor?: string;
    /**
     * **Android only** - Title of confirmation dialog
     */
    title?: string;
    /**
     * **Android only** - Color of fingerprint description
     */
    sensorColor?: string;
    /**
     * **Android only** - Color of fingerprint description after failed attempt
     */
    sensorErrorColor?: string;
    /**
     * **Android only** - Text shown next to the fingerprint image
     */
    sensorDescription?: string;
    /**
     * **Android only** - Text shown next to the fingerprint image after failed attempt
     */
    sensorErrorDescription?: string;
    /**
     * **Android only** - Cancel button text
     */
    cancelText?: string;
    /**
     * **Android only** - Cancel button text color
     */
    cancelTextColor?: string;
    /**
     * **iOS only** - By default specified 'Show Password' label. If set to empty string label is invisible.
     */
    fallbackLabel?: string;
    /**
     * **iOS only** - By default set to false. If set to true, will allow use of keypad passcode.
     */
    passcodeFallback?: boolean;
  }
  /**
   * `isSupported` error code
   */
  type IsSupportedErrorCode =
    | 'NOT_SUPPORTED'
    | 'NOT_AVAILABLE'
    | 'NOT_PRESENT'
    | 'NOT_ENROLLED';

  /**
   * `authenticate` error code
   */
  type AuthenticateErrorCode =
    | IsSupportedErrorCode
    | 'AUTHENTICATION_FAILED'
    | 'USER_CANCELED'
    | 'SYSTEM_CANCELED'
    | 'TIMEOUT'
    | 'LOCKOUT'
    | 'LOCKOUT_PERMANENT'
    | 'PROCESSING_ERROR'
    | 'USER_FALLBACK'
    | 'UNKNOWN_ERROR';

  /**
   * Error returned from `authenticate`
   */
  export interface AuthenticationError {
    name: 'TouchIDError';
    message: string;
    code: AuthenticateErrorCode;
    details: string;
  }
  /**
   * Error returned from `isSupported`
   */
  export interface IsSupportedError {
    name: 'TouchIDError';
    message: string;
    code: IsSupportedErrorCode;
    details: string;
  }

  const TouchID: {
    /**
     *
     * @param reason String that provides a clear reason for requesting authentication.
     * @param config Configuration object for more detailed dialog setup
     */
    authenticate(reason?: string, config?: AuthenticateConfig): Promise<true>;
    /**
     *
     * @param config - Returns a `Promise` that rejects if TouchID is not supported. On iOS resolves with a `biometryType` `String` of `FaceID` or `TouchID`
     */
    isSupported(config?: IsSupportedConfig): Promise<BiometryType>;
  };
  export default TouchID;
}
